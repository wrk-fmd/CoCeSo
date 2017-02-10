package at.wrk.coceso.plugins.gmaps;

import at.wrk.coceso.config.CocesoConfig;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GmapsWrapper {

  private final GeoApiContext context;

  @Autowired
  public GmapsWrapper(CocesoConfig config) {
    context = config.getGmapsApiKey() == null ? null : new GeoApiContext().setApiKey(config.getGmapsApiKey());
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

}
