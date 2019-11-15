package at.wrk.coceso.stomp.saga.message;

/**
 * This message type should be used for sending a notification to clients
 */
public interface NotificationMessage {

    /**
     * @return The name of the target exchange
     */
    String getTarget();

    /**
     * @return The routing key for the message
     */
    String getRoutingKey();

    /**
     * @return The payload data
     */
    Object getPayload();
}
