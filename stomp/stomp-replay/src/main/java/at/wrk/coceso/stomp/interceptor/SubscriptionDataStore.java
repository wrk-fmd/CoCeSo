package at.wrk.coceso.stomp.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * This class provides a mechanism to store incoming subscription information until for use on outgoing messages
 * TODO Locking mechanism might not work with many clients/subscriptions?
 */
@Component
public class SubscriptionDataStore {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, SubscriptionData> subscriptions = new HashMap<>();
    private final Map<String, String> ids = new HashMap<>();
    private final Object internalLock = new Object();

    /**
     * Add subscription information
     *
     * @param sessionId The session id for the subscription
     * @param subscriptionId The subscription id (passed by the client)
     * @param destination The destination for the subscription (passed by the client)
     * @param receipt The receipt requested by the client
     * @return An internal unique id to access the data
     */
    public String create(String sessionId, String subscriptionId, String destination, String queueName, String receipt) {
        synchronized (internalLock) {
            // Check if an id for this session/subscription/destination already exists
            String reverseKey = buildReverseKey(sessionId, subscriptionId);
            String previousId = ids.get(reverseKey);
            if (previousId != null) {
                LOG.warn("Subscription already existed for {}, {}, {}", sessionId, subscriptionId, destination);
                return previousId;
            }

            LOG.debug("Adding subscription information for {}", subscriptionId);
            String generatedId = generateId();
            subscriptions.put(generatedId, new SubscriptionData(sessionId, subscriptionId, destination, queueName, receipt));
            ids.put(reverseKey, generatedId);
            return generatedId;
        }
    }

    /**
     * Check if a given subscription is stored
     *
     * @param id The internal id of the subscription
     * @return True iff the subscription exists
     */
    public boolean exists(String id) {
        return subscriptions.containsKey(id);
    }

    /**
     * Get the internal id for a given session and subscription
     *
     * @param sessionId The session id of the subscription
     * @param subscriptionId The subscription id of the subscription
     * @return The internal id, or null iff it doesn't exist
     */
    public String getId(String sessionId, String subscriptionId) {
        return ids.get(buildReverseKey(sessionId, subscriptionId));
    }

    /**
     * Get the session id for a given id
     *
     * @param id The internal id of the subscription
     * @return The session id, or null iff it doesn't exist
     */
    public String getSessionId(String id) {
        SubscriptionData data = subscriptions.get(id);
        return data != null ? data.sessionId : null;
    }

    /**
     * Get the destination for a given id
     *
     * @param id The internal id of the subscription
     * @return The destination, or null iff it doesn't exist
     */
    public String getDestination(String id) {
        SubscriptionData data = subscriptions.get(id);
        return data != null ? data.destination : null;
    }

    /**
     * Get the queue name for a given id
     *
     * @param id The internal id of the subscription
     * @return The queue name, or null iff it doesn't exist
     */
    public String getQueueName(String id) {
        SubscriptionData data = subscriptions.get(id);
        return data != null ? data.queueName : null;
    }

    /**
     * Get the requested receipt for a given id
     *
     * @param id The id of the subscription
     * @return The requested receipt, or null iff it doesn't exist
     */
    public String getRequestedReceipt(String id) {
        SubscriptionData data = subscriptions.get(id);
        return data != null ? data.receipt : null;
    }

    /**
     * Remove an element from a queue, and remove the queue when it is empty
     *
     * @param id The internal id of the subscription
     * @return The removed message, or null if the queue was empty and has been removed
     */
    public Message<?> remove(String id) {
        synchronized (internalLock) {
            SubscriptionData data = subscriptions.get(id);
            if (data == null) {
                // No subscription with this id
                LOG.warn("Tried to delete non-existing subscription {}", id);
                return null;
            }

            if (data.queue.isEmpty()) {
                LOG.debug("Removing subscription data for {}", id);
                subscriptions.remove(id);
                ids.remove(buildReverseKey(data.sessionId, data.subscriptionId));
                return null;
            }

            LOG.debug("De-queuing message for {}", id);
            return data.queue.remove();
        }
    }

    /**
     * Add a message to a queue if it exists
     *
     * @param sessionId The session id for the subscription
     * @param subscriptionId The subscription id (passed by the client)
     * @param message The message
     * @return true iff the message has been queued
     */
    public boolean queue(String sessionId, String subscriptionId, Message<?> message) {
        synchronized (internalLock) {
            String reverseKey = buildReverseKey(sessionId, subscriptionId);

            String id = ids.get(reverseKey);
            if (id == null) {
                LOG.trace("No queue for {}/{} set", sessionId, subscriptionId);
                return false;
            }

            SubscriptionData data = subscriptions.get(id);
            Assert.state(data != null, "Internal id found, but no subscription data exists");

            LOG.debug("Queuing message for {}", id);
            data.queue.add(message);
            return true;
        }
    }

    private String generateId() {
        // This does not guarantee that a generated id has not been used before, but we can live with that
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (subscriptions.containsKey(uuid));
        return uuid;
    }

    private String buildReverseKey(String sessionId, String subscriptionId) {
        return sessionId + subscriptionId;
    }

    private static class SubscriptionData {

        private final String sessionId, subscriptionId, destination, queueName, receipt;
        private final Deque<Message<?>> queue;

        private SubscriptionData(String sessionId, String subscriptionId, String destination, String queueName, String receipt) {
            this.sessionId = sessionId;
            this.subscriptionId = subscriptionId;
            this.destination = destination;
            this.queueName = queueName;
            this.receipt = receipt;
            this.queue = new LinkedList<>();
        }
    }
}
