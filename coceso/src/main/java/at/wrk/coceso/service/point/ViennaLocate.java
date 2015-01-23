package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;

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
