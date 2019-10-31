package at.wrk.coceso.gateway.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
    public String create(String sessionId, String subscriptionId, String destination, String receipt) {
        synchronized (internalLock) {
            // Check if an id for this session/subscription/destination already exists
            String reverseKey = buildReverseKey(sessionId, subscriptionId, destination);
            String previousId = ids.get(reverseKey);
            if (previousId != null) {
                LOG.warn("Subscription already existed for {}, {}, {}", sessionId, subscriptionId, destination);
                return previousId;
            }

            LOG.debug("Adding subscription information for {}", subscriptionId);
            String generatedId = generateId();
            subscriptions.put(generatedId, new SubscriptionData(sessionId, subscriptionId, destination, receipt));
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
     * Get the subscription id for a given id
     *
     * @param id The internal id of the subscription
     * @return The subscription id, or null iff it doesn't exist
     */
    public String getSubscriptionId(String id) {
        SubscriptionData data = subscriptions.get(id);
        return data != null ? data.subscriptionId : null;
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
     * @return The removed message payload, or null if the queue was empty and has been removed
     */
    public Object remove(String id) {
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
                ids.remove(buildReverseKey(data.sessionId, data.subscriptionId, data.destination));
                return null;
            }

            LOG.debug("De-queuing message for {}", id);
            return data.queue.remove();
        }
    }

    /**
     * Add the initial data to the start of the queue if it exists
     *
     * @param id The internal id of the subscription
     * @param initialData A list of payload items (encoded for transport, e.g. byte[] using JSON)
     */
    public void addInitialData(String id, List<Object> initialData) {
        synchronized (internalLock) {
            SubscriptionData data = subscriptions.get(id);
            if (data == null) {
                LOG.warn("No queue found for {}, not adding initial data", id);
                return;
            }

            // Prepend each element to the queue
            LOG.debug("Adding initial data for {}", id);
            ListIterator<Object> it = initialData.listIterator(initialData.size());
            while (it.hasPrevious()) {
                data.queue.addFirst(it.previous());
            }
        }
    }

    /**
     * Add a message to a queue if it exists
     *
     * @param sessionId The session id for the subscription
     * @param subscriptionId The subscription id (passed by the client)
     * @param destination The destination for the subscription (passed by the client)
     * @param payload The payload of the message (encoded for transport, e.g. byte[] using JSON)
     * @return true iff the message has been queued
     */
    public boolean queue(String sessionId, String subscriptionId, String destination, Object payload) {
        synchronized (internalLock) {
            String id = ids.get(sessionId + subscriptionId + destination);
            if (id == null) {
                LOG.trace("No queue for {}/{}/{} set", sessionId, subscriptionId, destination);
                return false;
            }

            SubscriptionData data = subscriptions.get(id);
            Assert.state(data != null, "Internal id found, but no subscription data exists");

            LOG.debug("Queuing message for {}", id);
            data.queue.add(payload);
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

    private String buildReverseKey(String sessionId, String subscriptionId, String destination) {
        return sessionId + subscriptionId + destination;
    }

    private static class SubscriptionData {

        private final String sessionId, subscriptionId, destination, receipt;
        private final Deque<Object> queue;

        private SubscriptionData(String sessionId, String subscriptionId, String destination, String receipt) {
            this.sessionId = sessionId;
            this.subscriptionId = subscriptionId;
            this.destination = destination;
            this.receipt = receipt;
            this.queue = new LinkedList<>();
        }
    }
}
