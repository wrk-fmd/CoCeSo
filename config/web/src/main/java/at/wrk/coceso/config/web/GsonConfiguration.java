package at.wrk.coceso.config.web;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfiguration {
    @Bean
    public Gson createGsonBean() {
        return Converters.registerAll(new GsonBuilder()).create();
    }
}
