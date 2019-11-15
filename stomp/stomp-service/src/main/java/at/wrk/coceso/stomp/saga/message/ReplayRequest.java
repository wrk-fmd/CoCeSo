package at.wrk.coceso.stomp.saga.message;

/**
 * This message is sent when a new replay request was received
 */
public class ReplayRequest {

    private final String target;
    private final String recipient;
    private final String key;

    /**
     * @param target The name of the replayed exchange
     * @param recipient The name of the recipient queue
     * @param key The optional routing key for which the data should be replayed
     */
    public ReplayRequest(String target, String recipient, String key) {
        this.target = target;
        this.recipient = recipient;
        this.key = key;
    }

    /**
     * @return The name of the replayed exchange
     */
    public String getTarget() {
        return target;
    }

    /**
     * @return The name of the recipient queue
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @return The optional routing key for which the data should be replayed
     */
    public String getKey() {
        return key;
    }
}
