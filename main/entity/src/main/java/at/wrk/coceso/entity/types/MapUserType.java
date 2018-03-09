package at.wrk.coceso.entity.types;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;

public class MapUserType extends JsonUserType<Map> {

  @Override
  public Class<Map> returnedClass() {
    return Map.class;
  }

  @Override
  public Object deepCopy(Object o) throws HibernateException {
    if (o == null) {
      return null;
    }
    if (!(o instanceof Map)) {
      throw new HibernateException(String.format("Unable to deep copy object: not an instance of Map, is %s", o.getClass()));
    }
    return new HashMap<>((Map) o);
  }

}
