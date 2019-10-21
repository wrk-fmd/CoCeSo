package at.wrk.coceso.gateway.config;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.gateway.config.properties.BrokerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final BrokerProperties properties;
    private final ChannelInterceptor interceptor;

    @Autowired
    public WebSocketConfig(BrokerProperties properties, WebSocketChannelInterceptor interceptor) {
        this.properties = requireNonNull(properties, "BrokerProperties must not be null");
        this.interceptor = requireNonNull(interceptor, "ChannelInterceptor must not be null");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notifications").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/exchange")
            .setRelayHost(properties.getHost())
            .setClientLogin(properties.getUsername())
            .setClientPasscode(properties.getPassword())
            .setSystemLogin(properties.getUsername())
            .setSystemPasscode(properties.getPassword());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(interceptor);
    }

}
