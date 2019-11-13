package at.wrk.coceso.stomp.interceptor;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.Instant;

/**
 * This class intercepts incoming STOMP frames and modifies them before forwarding them to the broker
 */
@Component
public class StompInboundInterceptor extends AbstractStompInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int EXPIRES_MINUTES = 5;

    private final SubscriptionDataStore subscriptions;

    @Autowired
    public StompInboundInterceptor(SubscriptionDataStore subscriptions) {
        this.subscriptions = requireNonNull(subscriptions, "SubscriptionDataStore must not be null");
    }

    @Override
    protected Message<?> preSubscribe(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        // Intercept SUBSCRIBE frames
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();
        String destination = headers.getDestination();
        String requestedReceipt = headers.getReceipt();

        LOG.debug("Subscribing to {} for session {}", destination, sessionId);

        // TODO Make sure the subscription id is unique among all clients, authentication, ...
        // TODO Should incoming headers be filtered?

        // Make sure the old subscription is picked up again on reconnect
        headers.setNativeHeader("durable", "true");
        headers.setNativeHeader("auto-delete", "false");

        // Cleanup queues after some time
        headers.setNativeHeader("x-expires", Integer.toString(EXPIRES_MINUTES * 60 * 1000));

        if (shouldSendInitial(headers)) {
            // Store the subscription information
            String internalId = subscriptions.create(sessionId, subscriptionId, destination, requestedReceipt);

            // Request a receipt from the broker (using the unique internal id) which will trigger the replay
            headers.setReceipt(internalId);
        }

        return message;
    }

    private boolean shouldSendInitial(StompHeaderAccessor headers) {
        String lastConnectionHeader = headers.getFirstNativeHeader("last-connection");
        if (lastConnectionHeader == null || lastConnectionHeader.equals("null")) {
            // No last connection header given, resend
            return true;
        }

        try {
            Instant lastConnection = Instant.ofEpochSecond(Long.parseLong(lastConnectionHeader));

            // Check if last connection is older than the expiration duration, using 25% error margin
            return Instant.now().minus(Duration.ofSeconds(EXPIRES_MINUTES * 45)).isAfter(lastConnection);
        } catch (NumberFormatException e) {
            // Resend for invalid header values
            return true;
        }
    }
}
