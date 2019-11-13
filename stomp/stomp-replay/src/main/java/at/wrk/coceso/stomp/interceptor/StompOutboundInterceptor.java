package at.wrk.coceso.stomp.interceptor;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.stomp.replay.ReplayProvider;
import at.wrk.coceso.stomp.replay.ReplayProviderHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class intercepts outgoing STOMP frames and modifies them before forwarding them to the client
 */
@Component
public class StompOutboundInterceptor extends AbstractStompInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String INITIAL_PREFIX = "initial-";

    private final ReplayProviderHandler replayProviders;
    private final SubscriptionDataStore subscriptions;
    private final ObjectMapper mapper;

    @Autowired
    public StompOutboundInterceptor(ReplayProviderHandler replayProviders, SubscriptionDataStore subscriptions, ObjectMapper mapper) {
        this.replayProviders = requireNonNull(replayProviders, "ReplayProviderHandler must not be null");
        this.subscriptions = requireNonNull(subscriptions, "SubscriptionDataStore must not be null");
        this.mapper = requireNonNull(mapper, "MessageConverter must not be null");
    }

    @Override
    public Message<?> preMessage(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        // Intercept MESSAGE frames
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();
        String destination = headers.getDestination();
        LOG.trace("Intercepted message to {} for session {}", destination, sessionId);

        String messageId = headers.getMessageId();
        if (messageId != null && messageId.startsWith(INITIAL_PREFIX)) {
            // Directly forward messages sent through the initial replay logic
            return message;
        }

        if (subscriptions.queue(sessionId, subscriptionId, destination, message.getPayload())) {
            // Message has been queued, don't send it out immediately
            return null;
        }

        if (headers.isMutable()) {
            // Remove some Spring/AMQP headers to decrease message size
            headers.removeNativeHeader("__TypeId__");
            headers.removeNativeHeader("redelivered");
            headers.removeNativeHeader("priority");
            headers.removeNativeHeader("persistent");
        }

        return message;
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
        String subscriptionId = subscriptions.getSubscriptionId(id);
        String destination = subscriptions.getDestination(id);
        String requestedReceipt = subscriptions.getRequestedReceipt(id);

        // Check if subscription data is complete
        if (sessionId == null || subscriptionId == null || destination == null || !sessionId.equals(receiptSessionId)) {
            // Invalid subscription data: Do nothing
            LOG.warn("Data for {} in {} invalid: {}, {}, {}", id, receiptSessionId, sessionId, subscriptionId, destination);
            return null;
        }

        // This has to be done asynchronously, because otherwise the (ordered) channel would block
        // TODO use thread pool
        ReplaySender sender = new ReplaySender(channel, id, sessionId, subscriptionId, destination);
        new Thread(sender).start();

        if (requestedReceipt != null) {
            // Send receipt to client if requested
            headers.setReceiptId(requestedReceipt);
            return message;
        }

        return null;
    }

    private class ReplaySender implements Runnable, ExecutorChannelInterceptor {

        private static final String REPLAY_SENDER_HEADER = "replay-sender-id";

        private final MessageChannel channel;
        private final String id, sessionId, subscriptionId, destination;
        private final AtomicInteger index = new AtomicInteger();
        private Semaphore lock;

        public ReplaySender(MessageChannel channel, String id, String sessionId, String subscriptionId, String destination) {
            this.channel = channel;
            this.id = id;
            this.sessionId = sessionId;
            this.subscriptionId = subscriptionId;
            this.destination = destination;
        }

        @Override
        public void run() {
            LOG.debug("Replaying messages in {} for session {}", destination, sessionId);

            // Destinations are in the format "/exchanges/topic/[routingKey]"
            String[] parts = destination.split("/");
            if (parts.length >= 3) {
                // Get the provider for loading the initial status
                ReplayProvider<?> replayProvider = replayProviders.getProvider(parts[2]);
                if (replayProvider != null) {
                    try {
                        // Send messages for the initial status
                        List<Object> initialData = replayProvider.getMessages(parts.length >= 4 ? parts[3] : null).stream()
                                .map(this::toJson)
                                .collect(Collectors.toList());
                        subscriptions.addInitialData(id, initialData);
                    } catch (Exception e) {
                        // TODO Proper error handling
                        LOG.error("Exception on loading initial data for {}", destination, e);
                    }
                }
            }

            if (channel instanceof ExecutorSubscribableChannel) {
                // If channel provides a callback after message has been handled lock between messages to ensure the order of messages
                ((ExecutorSubscribableChannel) channel).addInterceptor(this);
                lock = new Semaphore(1);
            }

            // Send all data (initial and updates received since subscribing)
            while (true) {
                // Lock if using the interceptor to achieve ordered output
                acquireLock();

                Object queuedMessage = subscriptions.remove(id);
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
            Object replaySenderHeader = message.getHeaders().get(REPLAY_SENDER_HEADER);
            if (replaySenderHeader != null && replaySenderHeader.equals(id)) {
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

        private byte[] toJson(Object item) {
            try {
                return mapper.writeValueAsBytes(item);
            } catch (JsonProcessingException e) {
                throw new MessageConversionException("Could not write JSON: " + e.getMessage(), e);
            }
        }

        private boolean sendMessage(Object payload) {
            StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
            headers.setHeader(REPLAY_SENDER_HEADER, id);
            headers.setMessageId(INITIAL_PREFIX + id + index.incrementAndGet());
            headers.setSessionId(sessionId);
            headers.setSubscriptionId(subscriptionId);
            headers.setDestination(destination);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setLeaveMutable(true);
            return channel.send(MessageBuilder.createMessage(payload, headers.getMessageHeaders()));
        }
    }
}
