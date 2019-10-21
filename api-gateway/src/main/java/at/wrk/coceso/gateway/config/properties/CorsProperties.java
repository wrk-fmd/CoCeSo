package at.wrk.coceso.gateway.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.cors")
public class CorsProperties {

    private String[] origins;

    public String[] getOrigins() {
        return origins;
    }

    public void setOrigins(String[] origins) {
        this.origins = origins;
    }
}
