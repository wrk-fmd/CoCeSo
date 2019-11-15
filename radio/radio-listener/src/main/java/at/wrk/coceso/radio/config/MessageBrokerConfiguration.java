package at.wrk.coceso.radio.config;

import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.replay.ReplayConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBrokerConfiguration {

    @Bean
    public Exchange exchangeReceivedCall() {
        return new FanoutExchange(RadioQueueNames.CALLS_RECEIVED);
    }

    @Bean
    public Exchange exchangeReplayTrigger() {
        return new DirectExchange(ReplayConstants.REPLAY_TRIGGER_EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }
}
