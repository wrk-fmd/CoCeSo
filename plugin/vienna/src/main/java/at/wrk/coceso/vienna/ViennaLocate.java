package at.wrk.coceso.vienna;

import at.wrk.coceso.entity.Point;
import at.wrk.coceso.service.point.ILocate;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class ViennaLocate implements ILocate {

  @Override
  public boolean locate(Point p) {
    Point coordinates = new ViennaAddress(p).getCoordinates();
    if (coordinates != null) {
      p.setLatLong(coordinates);
      return true;
    }

    return false;
  }

}
