package at.wrk.coceso.radio.replay;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.replay.ReplayConstants;
import at.wrk.coceso.stomp.listener.AbstractReplayListener;
import at.wrk.coceso.stomp.saga.message.ReplayRequest;
import com.codebullets.sagalib.MessageStream;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue,
        exchange = @Exchange(ReplayConstants.REPLAY_TRIGGER_EXCHANGE),
        key = RadioQueueNames.CALLS_RECEIVED
))
public class RadioReplayListener extends AbstractReplayListener {

    private final MessageStream messageStream;

    @Autowired
    public RadioReplayListener(MessageStream messageStream) {
        this.messageStream = requireNonNull(messageStream, "MessageStream must not be null");
    }

    @Override
    protected void handleReplayRequest(String recipient, String key) {
        messageStream.add(new ReplayRequest(RadioQueueNames.CALLS_RECEIVED, recipient, key));
    }
}
