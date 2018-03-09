package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
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

  public static Patient initGroups(Patient patient) {
    init(patient.getIncidents(), Incident::getUnits);
    return patient;
  }

  public static <I extends Iterable<Patient>> I initGroups(I patients) {
    patients.forEach(patient -> initGroups(patient));
    return patients;
  }

}
