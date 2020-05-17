package at.wrk.coceso.config;

import at.wrk.coceso.dto.CocesoExchangeNames;
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
    public Exchange stompConcernsExchange() {
        return new FanoutExchange(CocesoExchangeNames.STOMP_CONCERNS);
    }

    @Bean
    public Exchange stompIncidentsExchange() {
        return new DirectExchange(CocesoExchangeNames.STOMP_INCIDENTS);
    }

    @Bean
    public Exchange stompUnitsExchange() {
        return new DirectExchange(CocesoExchangeNames.STOMP_UNITS);
    }

    @Bean
    public Exchange stompPatientsExchange() {
        return new DirectExchange(CocesoExchangeNames.STOMP_PATIENTS);
    }

    @Bean
    public Exchange stompContainersExchange() {
        return new DirectExchange(CocesoExchangeNames.STOMP_CONTAINERS);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }
}
