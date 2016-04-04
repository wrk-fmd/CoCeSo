package at.wrk.coceso.gmaps;

import at.wrk.coceso.config.CocesoConfig;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.service.point.ILocate;
import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class GmapsLocate implements ILocate {

  private final GeoApiContext context;

  @Autowired
  public GmapsLocate(CocesoConfig config) {
    context = config.getGmapsApiKey() == null ? null : new GeoApiContext().setApiKey(config.getGmapsApiKey());
  }

  @Override
  public boolean locate(Point p) {
    if (context != null) {
      Point coordinates = new GmapsAddress(p, context).getCoordinates();
      if (coordinates != null) {
        p.setLatLong(coordinates);
        return true;
      }
    }

    return false;
  }

}
