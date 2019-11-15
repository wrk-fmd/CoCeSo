package at.wrk.coceso.stomp.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.StringJoiner;

/**
 * This class intercepts incoming STOMP frames and modifies them before forwarding them to the broker
 */
@Component
public class StompInboundInterceptor extends AbstractStompInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int EXPIRES_MINUTES = 5;

    private final MessageDigest queueNameHash;

    @Autowired
    public StompInboundInterceptor() throws NoSuchAlgorithmException {
        this.queueNameHash = MessageDigest.getInstance("MD5");
    }

    @Override
    protected Message<?> preSubscribe(Message<?> message, StompHeaderAccessor headers) {
        // Intercept SUBSCRIBE frames
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();
        String destination = headers.getDestination();
        String requestedReceipt = headers.getReceipt();

        LOG.debug("Subscribing to {} for session {}", destination, sessionId);

        // TODO Make sure the subscription id is unique among all clients, authentication, ...
        // TODO Should incoming headers be filtered?

        // Generate a queue name for the subscription
        String queueName = generateQueueName(destination, subscriptionId);
        headers.setNativeHeader("x-queue-name", queueName);

        // Make sure the old subscription is picked up again on reconnect
        headers.setNativeHeader("durable", "true");
        headers.setNativeHeader("auto-delete", "false");

        // Cleanup queues after some time
        headers.setNativeHeader("x-expires", Integer.toString(EXPIRES_MINUTES * 60 * 1000));

        if (shouldSendInitial(headers)) {
            // Request a receipt, which encodes the information for triggering a replay
            StringJoiner receiptBuilder = new StringJoiner(":").add("replay").add(destination).add(queueName);

            if (requestedReceipt != null && !requestedReceipt.isBlank()) {
                // The client also requested a receipt, encode that too
                receiptBuilder.add(requestedReceipt);
            }

            String receipt = receiptBuilder.toString();

            LOG.debug("Requesting receipt {}", receipt);
            headers.setReceipt(receipt);
        }

        return message;
    }

    private String generateQueueName(String destination, String subscriptionId) {
        String name = destination + subscriptionId;
        String hashed = Base64.getUrlEncoder().encodeToString(queueNameHash.digest(name.getBytes()));
        return "coceso-stomp-" + hashed;
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
