package at.wrk.coceso.stomp.interceptor;

import static at.wrk.coceso.replay.ReplayConstants.REPLAY_TRIGGER_EXCHANGE;
import static at.wrk.coceso.replay.ReplayConstants.ROUTING_KEY_HEADER;
import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * This class intercepts outgoing STOMP frames and modifies them before forwarding them to the client
 */
@Component
public class StompOutboundInterceptor extends AbstractStompInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AmqpTemplate amqpTemplate;

    @Autowired
    public StompOutboundInterceptor(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = requireNonNull(amqpTemplate, "AmqpTemplate must not be null");
    }

    @Override
    public Message<?> preMessage(Message<?> message, StompHeaderAccessor headers) {
        // Intercept MESSAGE frames
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
    protected Message<?> preReceipt(Message<?> message, StompHeaderAccessor headers) {
        // Trigger message replay after a receipt for the subscription was received from the broker
        String id = headers.getReceiptId();
        if (id == null) {
            // Empty receipt: Do nothing
            LOG.warn("Received empty receipt for {}", headers.getSessionId());
            return null;
        }

        // TODO This would fail if the destination contained colons
        String[] receiptParts = id.split(":", 4);
        if (receiptParts.length < 3 || !receiptParts[0].equals("replay")) {
            // Not a replay receipt, just forward it to the client as is
            return message;
        }

        triggerReplay(receiptParts[1], receiptParts[2]);

        if (receiptParts.length >= 4) {
            // Send receipt to client if requested
            headers.setReceiptId(receiptParts[3]);
            return message;
        }

        return null;
    }

    private void triggerReplay(String destination, String queueName) {
        // Destinations are in the format "/exchanges/topic/[routingKey]"
        String[] destinationParts = destination.split("/");
        if (destinationParts.length < 3) {
            // No destination given, don't start replay
            LOG.debug("Destination {} incomplete, not triggering replay for {}", destination, queueName);
            return;
        }

        try {
            // Send a null-message (as JSON), using headers for the real information
            LOG.debug("Triggering replay of {} for {}", destination, queueName);
            amqpTemplate.convertAndSend(REPLAY_TRIGGER_EXCHANGE, destinationParts[2], "null", m -> {
                MessageProperties p = m.getMessageProperties();
                p.setContentType(MediaType.APPLICATION_JSON_VALUE);
                p.setReplyTo(queueName);
                p.setHeader(ROUTING_KEY_HEADER, destinationParts.length >= 4 ? destinationParts[3] : null);
                return m;
            });
        } catch (AmqpException e) {
            LOG.error("Error triggering initial data replay for {}, {}", destination, queueName, e);
        }
    }
}
