package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Initializer {
  private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);

  public static <T> T init(T entity, Function<T, Object>... accessors) {
    if (entity == null) {
      return null;
    }
    for (Function<T, Object> accessor : accessors) {
      if (accessor != null) {
        Hibernate.initialize(accessor.apply(entity));
      } else {
        LOG.warn("Tried to call null-accessor on entity '{}' for initialization.", entity);
      }
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
    patients.forEach(Initializer::initGroups);
    return patients;
  }

}
