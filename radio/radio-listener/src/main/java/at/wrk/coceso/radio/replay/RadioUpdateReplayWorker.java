package at.wrk.coceso.radio.replay;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.radio.service.RadioService;
import at.wrk.coceso.stomp.worker.AbstractUpdateReplayWorker;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;

@Component
public class RadioUpdateReplayWorker extends AbstractUpdateReplayWorker {

    private static final Duration duration = Duration.ofMinutes(5);

    private final RadioService radioService;

    public RadioUpdateReplayWorker(RadioService radioService, AmqpTemplate amqpTemplate) {
        super(amqpTemplate, RadioQueueNames.CALLS_RECEIVED);
        this.radioService = requireNonNull(radioService, "RadioService must not be null");
    }

    @Override
    protected Collection<ReceivedCallDto> getData(String routingKey) {
        return radioService.getLast(duration);
    }
}
