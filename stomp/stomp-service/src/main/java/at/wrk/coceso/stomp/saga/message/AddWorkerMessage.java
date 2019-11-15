package at.wrk.coceso.stomp.saga.message;

import at.wrk.coceso.stomp.worker.UpdateReplayWorker;

/**
 * This message is sent when a new worker has been added
 */
public class AddWorkerMessage {

    private final String target;
    private final UpdateReplayWorker worker;

    /**
     * @param target The target of the new worker
     * @param worker The worker itself
     */
    public AddWorkerMessage(String target, UpdateReplayWorker worker) {
        this.target = target;
        this.worker = worker;
    }

    /**
     * @return The target of the new worker
     */
    public String getTarget() {
        return target;
    }

    /**
     * @return The worker itself
     */
    public UpdateReplayWorker getWorker() {
        return worker;
    }
}
