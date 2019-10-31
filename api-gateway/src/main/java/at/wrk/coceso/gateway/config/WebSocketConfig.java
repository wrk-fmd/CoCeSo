package at.wrk.coceso.gateway.config;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.gateway.config.properties.BrokerProperties;
import at.wrk.coceso.gateway.stomp.StompInboundInterceptor;
import at.wrk.coceso.gateway.stomp.StompOutboundInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final BrokerProperties properties;
    private final StompInboundInterceptor inboundInterceptor;
    private final StompOutboundInterceptor outboundInterceptor;

    @Autowired
    public WebSocketConfig(BrokerProperties properties, StompInboundInterceptor inboundInterceptor,
            StompOutboundInterceptor outboundInterceptor) {
        this.properties = requireNonNull(properties, "BrokerProperties must not be null");
        this.inboundInterceptor = requireNonNull(inboundInterceptor, "StompInboundInterceptor must not be null");
        this.outboundInterceptor = requireNonNull(outboundInterceptor, "StompOutboundInterceptor must not be null");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notifications").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setPreservePublishOrder(true)
                .enableStompBrokerRelay("/exchange")
                .setRelayHost(properties.getHost())
                .setClientLogin(properties.getUsername())
                .setClientPasscode(properties.getPassword())
                .setSystemLogin(properties.getUsername())
                .setSystemPasscode(properties.getPassword());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundInterceptor);
    }
}
