package at.wrk.coceso.stomp.worker;

/**
 * This is the a worker which sends updates and replays initial data
 */
public interface UpdateReplayWorker extends Runnable, AutoCloseable {

    /**
     * Add an update to the queue for sending
     *
     * @param update The update payload
     * @param routingKey The optional routing key to use
     */
    void addUpdate(Object update, String routingKey);

    /**
     * Request a replay to be sent
     *
     * @param recipient The recipient for the replay
     * @param key The optional key to determine the replayed data
     */
    void requestReplay(String recipient, String key);
}
