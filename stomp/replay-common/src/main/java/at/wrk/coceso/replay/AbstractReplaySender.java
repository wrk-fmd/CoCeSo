package at.wrk.coceso.replay;

import static at.wrk.coceso.replay.ReplayConstants.REPLAY_HEADER;
import static at.wrk.coceso.replay.ReplayConstants.REPLAY_HEADER_ACTIVE;
import static at.wrk.coceso.replay.ReplayConstants.REPLAY_HEADER_DONE;
import static at.wrk.coceso.replay.ReplayConstants.ROUTING_KEY_HEADER;
import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpMessageHeaderAccessor;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

/**
 * This is the basis for a Replay Sender which should be overridden in each component
 * Annotate the overriding class with {@link RabbitListener}, specifying the queue on which the replay requests are received.
 */
public abstract class AbstractReplaySender {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RabbitTemplate rabbitTemplate;

    public AbstractReplaySender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = requireNonNull(rabbitTemplate, "RabbitTemplate must not be null");
    }

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

        LOG.debug("Replaying messages to {}", replyTo);
        getData(routingKey).forEach(item -> send(replyTo, item, false));
        send(replyTo, "", true);
    }

    protected abstract Collection<?> getData(String routingKey);

    private void send(String queue, Object item, boolean done) {
        rabbitTemplate.convertAndSend(queue, item, m -> {
            m.getMessageProperties().setHeader(REPLAY_HEADER, done ? REPLAY_HEADER_DONE : REPLAY_HEADER_ACTIVE);
            return m;
        });
    }
}
