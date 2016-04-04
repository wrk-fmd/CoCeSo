package at.wrk.coceso.entityevent;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.EntityEventHandler;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class NotifyList {

  private final Set<Incident> incidents;
  private final Set<Patient> patients;
  private final Set<Unit> units;

  public NotifyList() {
    incidents = new HashSet<>();
    patients = new HashSet<>();
    units = new HashSet<>();
  }

  public void add(Incident incident) {
    incidents.add(incident);
  }

  public void add(Patient patient) {
    patients.add(patient);
  }

  public void add(Unit unit) {
    units.add(unit);
  }

  public void addAllIncidents(Collection<Incident> incident) {
    incidents.addAll(incident);
  }

  public void addAllPatients(Collection<Patient> patient) {
    patients.addAll(patient);
  }

  public void addAllUnits(Collection<Unit> unit) {
    units.addAll(unit);
  }

  public synchronized void sendNotifications() {
    sendNotifications(incidents, Incident.class);
    sendNotifications(patients, Patient.class);
    sendNotifications(units, Unit.class);
  }

  private synchronized <T> void sendNotifications(Set<T> entities, Class<T> type) {
    EntityEventHandler<T> entityEventHandler = EntityEventHandler.getInstance(type);
    entities.forEach(e -> entityEventHandler.entityChanged(e));
    entities.clear();
  }

  public static <T> T execute(Function<NotifyList, T> function) {
    NotifyList notify = new NotifyList();
    T ret = function.apply(notify);
    notify.sendNotifications();
    return ret;
  }

  public static void executeVoid(Consumer<NotifyList> function) {
    NotifyList notify = new NotifyList();
    function.accept(notify);
    notify.sendNotifications();
  }

}
