package at.wrk.coceso.stomp.interceptor;

import static at.wrk.coceso.replay.ReplayConstants.REPLAY_HEADER;
import static at.wrk.coceso.replay.ReplayConstants.REPLAY_HEADER_ACTIVE;
import static at.wrk.coceso.replay.ReplayConstants.REPLAY_HEADER_DONE;
import static at.wrk.coceso.replay.ReplayConstants.REPLAY_TRIGGER_EXCHANGE;
import static at.wrk.coceso.replay.ReplayConstants.ROUTING_KEY_HEADER;
import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * This class intercepts outgoing STOMP frames and modifies them before forwarding them to the client
 */
@Component
public class StompOutboundInterceptor extends AbstractStompInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SubscriptionDataStore subscriptions;
    private final RabbitTemplate template;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Autowired
    public StompOutboundInterceptor(SubscriptionDataStore subscriptions, RabbitTemplate rabbitTemplate) {
        this.subscriptions = requireNonNull(subscriptions, "SubscriptionDataStore must not be null");
        this.template = requireNonNull(rabbitTemplate, "RabbitTemplate must not be null");
    }

    @Override
    public Message<?> preMessage(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        // Intercept MESSAGE frames
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();
        LOG.trace("Intercepted message to {} for session {}", subscriptionId, sessionId);

        // Check if this message comes from the service's replay logic
        String replayHeader = headers.getFirstNativeHeader(REPLAY_HEADER);
        if (REPLAY_HEADER_DONE.equals(replayHeader)) {
            // Initial replay done, resend all queued updates
            finishReplay(channel, subscriptions.getId(sessionId, subscriptionId));
            return null;
        }

        if (REPLAY_HEADER_ACTIVE.equals(replayHeader)) {
            // Directly forward messages sent through the initial replay logic
            cleanupHeaders(headers);
            return message;
        }

        if (subscriptions.queue(sessionId, subscriptionId, message)) {
            // Message has been queued, don't send it out immediately
            return null;
        }

        cleanupHeaders(headers);
        return message;
    }

    private void cleanupHeaders(StompHeaderAccessor headers) {
        if (headers.isMutable()) {
            // Remove some Spring/AMQP headers to decrease message size
            headers.removeNativeHeader("__TypeId__");
            headers.removeNativeHeader("redelivered");
            headers.removeNativeHeader("priority");
            headers.removeNativeHeader("persistent");
            headers.removeNativeHeader(REPLAY_HEADER);
        }
    }

    @Override
    protected Message<?> preReceipt(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        // Trigger message replay after a receipt for the subscription was received from the broker
        String id = headers.getReceiptId();
        String receiptSessionId = headers.getSessionId();
        if (id == null) {
            // Empty receipt: Do nothing
            LOG.warn("Received empty receipt for {}", receiptSessionId);
            return null;
        }

        // Check if subscription for the given receipt is known
        if (!subscriptions.exists(id)) {
            // Unknown receipt: Just forward it to the client
            LOG.debug("Received unknown receipt id {} in {}", id, receiptSessionId);
            return message;
        }

        // Load subscription data (might have been deleted since the last if statement, but that doesn't matter)
        String sessionId = subscriptions.getSessionId(id);
        String destination = subscriptions.getDestination(id);
        String queueName = subscriptions.getQueueName(id);
        String requestedReceipt = subscriptions.getRequestedReceipt(id);

        // Check if subscription data is complete
        if (sessionId == null || destination == null || queueName == null || !sessionId.equals(receiptSessionId)) {
            // Invalid subscription data: Do nothing
            LOG.warn("Data for {} in {} invalid: {}, {}, {}", id, receiptSessionId, sessionId, destination, queueName);
            return null;
        }

        if (!triggerReplay(destination, queueName)) {
            // No replay triggered, finish immediately
            finishReplay(channel, id);
        }

        if (requestedReceipt != null) {
            // Send receipt to client if requested
            headers.setReceiptId(requestedReceipt);
            return message;
        }

        return null;
    }

    private boolean triggerReplay(String destination, String queueName) {
        // Destinations are in the format "/exchanges/topic/[routingKey]"
        String[] parts = destination.split("/");
        if (parts.length < 3) {
            // No destination given, don't start replay
            return false;
        }

        try {
            // Send a null-message (as JSON), using headers for the real information
            template.convertAndSend(REPLAY_TRIGGER_EXCHANGE, parts[2], "null", m -> {
                MessageProperties p = m.getMessageProperties();
                p.setContentType(MediaType.APPLICATION_JSON_VALUE);
                p.setReplyTo(queueName);
                p.setHeader(ROUTING_KEY_HEADER, parts.length >= 4 ? parts[3] : null);
                return m;
            });
        } catch (AmqpException e) {
            LOG.error("Error triggering initial data replay for {}, {}", destination, queueName, e);
            return false;
        }

        return true;
    }

    private void finishReplay(MessageChannel channel, String id) {
        // This has to be done asynchronously, because otherwise the (ordered) channel would block
        if (id != null) {
            threadPool.submit(new QueuedMessagesSender(channel, id));
        }
    }

    private class QueuedMessagesSender implements Runnable, ExecutorChannelInterceptor {

        private static final String QUEUED_SENDER_HEADER = "x-queued-sender";

        private final MessageChannel channel;
        private final String id;
        private Semaphore lock;

        public QueuedMessagesSender(MessageChannel channel, String id) {
            this.channel = channel;
            this.id = id;
        }

        @Override
        public void run() {
            LOG.debug("Sending queued messages for {}", id);

            if (channel instanceof ExecutorSubscribableChannel) {
                // If channel provides a callback after message has been handled lock between messages to ensure the order of messages
                ((ExecutorSubscribableChannel) channel).addInterceptor(this);
                lock = new Semaphore(1);
            }

            // Send all data (initial and updates received since subscribing)
            while (true) {
                // Lock if using the interceptor to achieve ordered output
                acquireLock();

                Message<?> queuedMessage = subscriptions.remove(id);
                if (queuedMessage == null) {
                    // No more queued messages
                    break;
                }

                if (!sendMessage(queuedMessage)) {
                    // Message will not be sent, unlock immediately
                    releaseLock();
                }
            }

            if (channel instanceof ExecutorSubscribableChannel) {
                ((ExecutorSubscribableChannel) channel).removeInterceptor(this);
            }
        }

        @Override
        public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
            Object replaySenderHeader = message.getHeaders().get(QUEUED_SENDER_HEADER);
            if (replaySenderHeader == this) {
                releaseLock();
            }
        }

        private void acquireLock() {
            if (lock == null) {
                return;
            }

            while (true) {
                try {
                    lock.acquire();
                    return;
                } catch (InterruptedException e) {
                    LOG.warn("Interrupted while waiting for send lock", e);
                }
            }
        }

        private void releaseLock() {
            if (lock != null) {
                lock.release();
            }
        }

        private boolean sendMessage(Message<?> message) {
            StompHeaderAccessor headers = getHeaders(message);
            headers.setHeader(QUEUED_SENDER_HEADER, this);
            headers.setNativeHeader(REPLAY_HEADER, REPLAY_HEADER_ACTIVE);
            headers.setLeaveMutable(true);
            return channel.send(message);
        }
    }
}
