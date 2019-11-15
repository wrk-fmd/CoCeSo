package at.wrk.coceso.stomp.config;

import at.wrk.coceso.replay.ReplayConstants;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {

    @Bean
    public Exchange exchangeReceivedCall() {
        return new DirectExchange(ReplayConstants.REPLAY_TRIGGER_EXCHANGE);
    }
}
