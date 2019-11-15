package at.wrk.coceso.stomp.saga.message;

import javax.annotation.Nullable;

/**
 * This class provides a default implementation for sending notifications to clients
 * It can be overridden in order to get more specific behaviour
 */
public class DefaultNotificationMessage implements NotificationMessage {

    private final String target;
    private final String routingKey;
    private final Object payload;

    /**
     * Send a notification to an exchange without a routing key
     *
     * @param target The name of the target exchange
     * @param payload The payload data
     */
    public DefaultNotificationMessage(String target, Object payload) {
        this(target, null, payload);
    }

    /**
     * Send a notification to an exchange target
     *
     * @param target The name of the target exchange
     * @param routingKey The routing key for the message
     * @param payload The payload data
     */
    public DefaultNotificationMessage(String target, @Nullable String routingKey, Object payload) {
        this.target = target;
        this.routingKey = routingKey;
        this.payload = payload;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}
