package at.wrk.coceso.utils;

import java.util.function.Function;
import org.hibernate.Hibernate;

public class Initializer {

  public static <T> T init(T entity, Function<T, Object>... accessors) {
    if (entity == null) {
      return null;
    }
    for (Function<T, Object> accessor : accessors) {
      Hibernate.initialize(accessor.apply(entity));
    }
    return entity;
  }

  public static <I extends Iterable<T>, T> I init(I iterable, Function<T, Object>... accessors) {
    iterable.forEach(e -> init(e, accessors));
    return iterable;
  }

}
