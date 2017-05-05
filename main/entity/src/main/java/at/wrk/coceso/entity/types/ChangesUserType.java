package at.wrk.coceso.entity.types;

import at.wrk.coceso.entity.helper.Changes;
import org.hibernate.HibernateException;

public class ChangesUserType extends JsonUserType<Changes> {

  @Override
  public Class<Changes> returnedClass() {
    return Changes.class;
  }

  @Override
  public Object deepCopy(Object o) throws HibernateException {
    if (o == null) {
      return null;
    }
    if (!(o instanceof Changes)) {
      throw new HibernateException(String.format("Unable to deep copy object: not an instance of Changes, is %s", o.getClass()));
    }
    return ((Changes) o).deepCopy();
  }

}
