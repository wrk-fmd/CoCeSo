package at.wrk.coceso.stomp.config;

import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This class creates Beans for accessing the REST interfaces of the other services
 */
@Configuration
class RestConfiguration {

    private final ServiceProperties properties;
    private final RestTemplateBuilder builder;

    @Autowired
    public RestConfiguration(ServiceProperties properties, RestTemplateBuilder builder) {
        this.properties = requireNonNull(properties, "ServiceProperties must not be null");
        this.builder = requireNonNull(builder, "RestTemplateBuilder must not be null");
    }

    /**
     * Create a RestTemplate for the Radio application
     */
    @Bean
    public RestTemplate radioTemplate() {
        return builder.rootUri(properties.getRadio()).build();
    }
}
