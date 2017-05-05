package at.wrk.coceso.entity.types;

import at.wrk.coceso.entity.point.Point;
import org.hibernate.HibernateException;

public class PointUserType extends JsonUserType<Point> {

  @Override
  public Class<Point> returnedClass() {
    return Point.class;
  }

  @Override
  public Object deepCopy(Object o) throws HibernateException {
    if (o == null) {
      return null;
    }
    if (!(o instanceof Point)) {
      throw new HibernateException(String.format("Unable to deep copy object: not an instance of Point, is %s", o.getClass()));
    }
    return ((Point) o).deepCopy();
  }

}
