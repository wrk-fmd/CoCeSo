package at.wrk.coceso.radio.replay;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.radio.service.RadioService;
import at.wrk.coceso.replay.AbstractReplaySender;
import at.wrk.coceso.replay.ReplayConstants;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue,
        exchange = @Exchange(ReplayConstants.REPLAY_TRIGGER_EXCHANGE),
        key = RadioQueueNames.CALLS_RECEIVED
))
public class RadioReplaySender extends AbstractReplaySender {

    private static final Duration duration = Duration.ofMinutes(5);

    private final RadioService radioService;

    @Autowired
    public RadioReplaySender(RadioService radioService, RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
        this.radioService = requireNonNull(radioService, "RadioService must not be null");
    }

    @Override
    protected Collection<?> getData(String routingKey) {
        return radioService.getLast(duration);
    }
}
