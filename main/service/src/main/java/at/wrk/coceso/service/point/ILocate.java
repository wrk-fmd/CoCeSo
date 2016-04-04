package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Point;

public interface ILocate {

  public boolean locate(Point p);

  public default boolean locate(Point p, Concern concern) {
    return locate(p);
  }
}
