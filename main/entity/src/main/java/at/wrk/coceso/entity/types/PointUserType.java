package at.wrk.coceso.entity.types;

import at.wrk.coceso.entity.point.Point;

public class PointUserType extends JsonUserType<Point> {

  @Override
  public Class<Point> returnedClass() {
    return Point.class;
  }

}
