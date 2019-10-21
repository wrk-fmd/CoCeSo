package at.wrk.coceso.gateway.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * This class intercepts STOMP message frames and modifies them before forwarding them to the broker
 */
@Component
class WebSocketChannelInterceptor implements ChannelInterceptor {

    private static final String COMMAND_HEADER = "stompCommand";
    private static final String NATIVE_HEADERS = "nativeHeaders";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        StompCommand command = headers.get(COMMAND_HEADER, StompCommand.class);
        if (command == StompCommand.SUBSCRIBE) {
            // Intercept SUBSCRIBE frames
            @SuppressWarnings("unchecked")
            MultiValueMap<String, String> nativeHeaders = headers.get(NATIVE_HEADERS, MultiValueMap.class);

            // Make sure the old subscription is picked up again on reconnect
            nativeHeaders.set("durable", "true");
            nativeHeaders.set("auto-delete", "false");

            // TODO Make sure the subscription id is unique among all clients, authentication, ...

            // Return the modified message
            message = MessageBuilder.fromMessage(message)
                    .setHeader(NATIVE_HEADERS, nativeHeaders)
                    .build();
        }

        return message;
    }
}