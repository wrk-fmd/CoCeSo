package at.wrk.coceso.stomp.saga;

import at.wrk.coceso.stomp.saga.message.AddWorkerMessage;
import at.wrk.coceso.stomp.saga.message.NotificationMessage;
import at.wrk.coceso.stomp.saga.message.ReplayRequest;
import com.codebullets.sagalib.AbstractSaga;
import com.codebullets.sagalib.EventHandler;
import com.codebullets.sagalib.KeyReader;
import com.codebullets.sagalib.KeyReaders;
import com.codebullets.sagalib.StartsSaga;

import java.util.Arrays;
import java.util.Collection;

/**
 * This Saga is used for sending updates and replays using a worker
 */
public class UpdateReplaySaga extends AbstractSaga<UpdateReplayState> {

    /**
     * Start a Saga instance for a newly added worker
     *
     * @param message The information about the added worker
     */
    @StartsSaga
    public void addWorker(AddWorkerMessage message) {
        state().addInstanceKey(message.getTarget());
        state().worker(message.getWorker());
    }

    /**
     * Send a notification message
     *
     * @param message The message information
     */
    @EventHandler
    public void notificationMessage(NotificationMessage message) {
        state().getWorker().addUpdate(message.getPayload(), message.getRoutingKey());
    }

    /**
     * Request a replay of the initial data
     *
     * @param request The replay request information
     */
    @EventHandler
    public void replayRequest(ReplayRequest request) {
        state().getWorker().requestReplay(request.getRecipient(), request.getKey());
    }

    @Override
    public void createNewState() {
        setState(new UpdateReplayState());
    }

    @Override
    public Collection<KeyReader> keyReaders() {
        return Arrays.asList(
                KeyReaders.forMessage(NotificationMessage.class, NotificationMessage::getTarget),
                KeyReaders.forMessage(ReplayRequest.class, ReplayRequest::getTarget)
        );
    }
}
