package at.wrk.coceso.gateway.config;

import static java.util.Objects.requireNonNull;

import java.util.List;

import at.wrk.coceso.gateway.config.properties.CorsProperties;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class sets the CORS headers for the origins given in the config file
 */
@Configuration
@EnableWebMvc
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

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).getObjectMapper()
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        }
    }
}
