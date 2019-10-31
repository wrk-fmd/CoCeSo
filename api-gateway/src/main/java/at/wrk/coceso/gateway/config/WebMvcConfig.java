package at.wrk.coceso.gateway.config;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.gateway.config.properties.CorsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class sets the CORS headers for the origins given in the config file
 */
@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    private final CorsProperties properties;

    @Autowired
    public WebMvcConfig(CorsProperties properties) {
        this.properties = requireNonNull(properties, "CorsProperties must not be null");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO restrict to specific origins
        registry.addMapping("/**").allowedMethods("*");
    }
}
