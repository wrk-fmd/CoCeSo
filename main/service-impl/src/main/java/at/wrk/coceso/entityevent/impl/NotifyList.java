package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.utils.Initializer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class NotifyList {

  private final EntityEventFactory eef;

  private final Set<Incident> incidents;
  private final Set<Patient> patients;
  private final Set<Unit> units;

  public NotifyList(EntityEventFactory eef) {
    this.eef = eef;
    incidents = new HashSet<>();
    patients = new HashSet<>();
    units = new HashSet<>();
  }

  public void add(Incident incident) {
    Initializer.init(incident, Incident::getConcern, Incident::getUnits, Incident::getPatient);
    incidents.add(incident);
  }

  public void add(Patient patient) {
    Initializer.init(patient, Patient::getConcern, Patient::getIncidents);
    patients.add(patient);
  }

  public void add(Unit unit) {
    Initializer.init(unit, Unit::getConcern, Unit::getIncidents);
    units.add(unit);
  }

  public void addAllIncidents(Collection<Incident> incidents) {
    Initializer.init(incidents, Incident::getConcern, Incident::getUnits, Incident::getPatient);
    incidents.addAll(incidents);
  }

  public void addAllPatients(Collection<Patient> patients) {
    Initializer.init(patients, Patient::getConcern, Patient::getIncidents);
    patients.addAll(patients);
  }

  public void addAllUnits(Collection<Unit> units) {
    Initializer.init(units, Unit::getConcern, Unit::getIncidents);
    units.addAll(units);
  }

  public synchronized void sendNotifications() {
    sendNotifications(incidents, Incident.class);
    sendNotifications(patients, Patient.class);
    sendNotifications(units, Unit.class);
  }

  private synchronized <T> void sendNotifications(Set<T> entities, Class<T> type) {
    EntityEventHandler<T> entityEventHandler = eef.getEntityEventHandler(type);
    entities.forEach(e -> entityEventHandler.entityChanged(e));
    entities.clear();
  }

  public static <T> T execute(Function<NotifyList, T> function, EntityEventFactory eef) {
    NotifyList notify = new NotifyList(eef);
    T ret = function.apply(notify);
    notify.sendNotifications();
    return ret;
  }

  public static void executeVoid(Consumer<NotifyList> function, EntityEventFactory eef) {
    NotifyList notify = new NotifyList(eef);
    function.accept(notify);
    notify.sendNotifications();
  }

}
