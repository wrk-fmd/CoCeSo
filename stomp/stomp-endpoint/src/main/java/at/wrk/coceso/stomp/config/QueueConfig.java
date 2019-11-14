package at.wrk.coceso.stomp.config;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "application")
public class QueueConfig {

    private Map<String, String> services;

    public Map<String, String> getServices() {
        return services;
    }

    public void setServices(Map<String, String> services) {
        this.services = services;
    }

    @Bean
    public Map<String, Queue> triggerReplayQueues() {
        // Declare the replay trigger queue for each service and store it in a map
        return services.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> new Queue(e.getValue())));
    }
}
