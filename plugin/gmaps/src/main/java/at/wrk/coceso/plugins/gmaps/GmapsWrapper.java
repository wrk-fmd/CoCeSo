package at.wrk.coceso.plugins.gmaps;

import at.wrk.geocode.GeocodeConfig;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class GmapsWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(GmapsWrapper.class);

    private final GeoApiContext context;

    @Autowired
    public GmapsWrapper(final GeocodeConfig config, final GeoApiContextFactory geoApiContextFactory) {
        if (config.getGmapsApiKey() == null) {
            LOG.info("Configuration file does not contain a Google API key. Google geocoding is disabled.");
            this.context = null;
        } else {
            LOG.info("Google API key is loaded and Google geocoding is enabled.");
            this.context =  geoApiContextFactory.create(config.getGmapsApiKey());
        }
    }

    public GeocodingResult[] geocode(String query) throws Exception {
        return GeocodingApi.geocode(context, query).await();
    }

    public GeocodingResult[] reverseGeocode(double lat, double lng) throws Exception {
        return GeocodingApi.reverseGeocode(context, new LatLng(lat, lng)).await();
    }

    public boolean isActive() {
        return context != null;
    }

    @PreDestroy
    public void preDestroy() {
        LOG.info("shutting down Google Maps wrapper.");
        if (this.context != null) {
            this.context.shutdown();
        }
    }
}
