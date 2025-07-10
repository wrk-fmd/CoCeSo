package at.wrk.coceso.config.tomcat10;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
public class Tomcat10Configuration {

    @Bean
    public HandshakeHandler getHandshakeHandler() {
        return new DefaultHandshakeHandler(new Tomcat10RequestUpgradeStrategy());
    }
}
