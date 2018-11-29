package at.wrk.coceso.plugins.gmaps;

import com.google.maps.GeoApiContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GeoApiContextFactory {
    public GeoApiContext create(final String apiKey) {
        return new GeoApiContext()
                .setApiKey(apiKey)
                .setConnectTimeout(2, TimeUnit.SECONDS)
                .setReadTimeout(2, TimeUnit.SECONDS)
                .setWriteTimeout(2, TimeUnit.SECONDS)
                .setRetryTimeout(3, TimeUnit.SECONDS);
    }
}
