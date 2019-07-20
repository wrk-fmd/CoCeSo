package at.wrk.coceso.plugins.gmaps;

import com.google.maps.GeoApiContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GeoApiContextFactory {
    public GeoApiContext create(final String apiKey) {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .retryTimeout(3, TimeUnit.SECONDS)
                .build();
    }
}
