package at.wrk.coceso.stomp.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

abstract class AbstractStompInterceptor implements ChannelInterceptor {

    /**
     * Intercepts messages before they are forwarded
     *
     * @param message The message
     * @param channel The channel
     * @return The modified message, or null if it should not be sent
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headers = getHeaders(message);
        StompCommand command = headers.getCommand();

        if (command == null) {
            // This happens for heartbeats, just ignore them
            return message;
        }

        switch (command) {
            case SUBSCRIBE:
                return preSubscribe(message, channel, headers);
            case MESSAGE:
                return preMessage(message, channel, headers);
            case RECEIPT:
                return preReceipt(message, channel, headers);
            default:
                return message;
        }
    }

    /**
     * Called before SUBSCRIBE frames are forwarded
     *
     * @param message The message
     * @param headers The headers accessor
     * @return The modified message, or null if it should not be sent
     */
    protected Message<?> preSubscribe(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        return message;
    }

    /**
     * Called before MESSAGE frames are forwarded
     *
     * @param message The message
     * @param headers The headers accessor
     * @return The modified message, or null if it should not be sent
     */
    protected Message<?> preMessage(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        return message;
    }

    /**
     * Called before RECEIPT frames are forwarded
     *
     * @param message The message
     * @param headers The headers accessor
     * @return The modified message, or null if it should not be sent
     */
    protected Message<?> preReceipt(Message<?> message, MessageChannel channel, StompHeaderAccessor headers) {
        return message;
    }

    protected StompHeaderAccessor getHeaders(Message<?> message) {
        StompHeaderAccessor headers = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (headers == null) {
            throw new IllegalArgumentException("Not a STOMP message (could not extract headers)");
        }
        return headers;
    }
}
