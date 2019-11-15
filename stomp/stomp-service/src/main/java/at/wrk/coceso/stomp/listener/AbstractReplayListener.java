package at.wrk.coceso.stomp.listener;

import static at.wrk.coceso.replay.ReplayConstants.ROUTING_KEY_HEADER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;

import java.lang.invoke.MethodHandles;

/**
 * This is the basis for a Replay Sender which should be overridden in each component
 * Annotate the overriding class with {@link RabbitListener}, specifying the queue on which the replay requests are received.
 */
public abstract class AbstractReplayListener {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @RabbitHandler
    public void listen(@SuppressWarnings("unused") Object payload, AmqpMessageHeaderAccessor headers) {
        // For some reason Spring does not detect the method if payload is not present as parameter
        String replyTo = headers.getReplyTo();
        String routingKey = headers.getFirstNativeHeader(ROUTING_KEY_HEADER);
        if (replyTo == null) {
            // No return address given, do nothing
            LOG.info("Received replay request without replyTo address");
            return;
        }

        // Trigger the handler for the request
        handleReplayRequest(replyTo, routingKey);
    }

    protected abstract void handleReplayRequest(String recipient, String key);
}
