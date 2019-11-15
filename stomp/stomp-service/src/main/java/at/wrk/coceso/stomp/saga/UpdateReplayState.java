package at.wrk.coceso.stomp.saga;

import at.wrk.coceso.stomp.worker.UpdateReplayWorker;
import com.codebullets.sagalib.AbstractSagaState;

/**
 * This class represents the state of a Saga
 */
class UpdateReplayState extends AbstractSagaState<String> {

    private UpdateReplayWorker worker;

    /**
     * @return The worker assigned to this Saga
     */
    public UpdateReplayWorker getWorker() {
        return worker;
    }

    /**
     * Update the worker assigned to this Saga
     *
     * @param worker The new worker instance
     */
    public void worker(UpdateReplayWorker worker) {
        this.worker = worker;
    }
}
