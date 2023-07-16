package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.utils.Initializer;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotifyList {

  private final EntityEventFactory entityEventFactory;
  private final UnitRepository unitRepository;

  private final Set<Incident> incidents;
  private final Set<Patient> patients;
  private final Set<Integer> unitIds;

  @Autowired
  public NotifyList(
          final EntityEventFactory entityEventFactory,
          final UnitRepository unitRepository) {
    this.entityEventFactory = entityEventFactory;
    this.unitRepository = unitRepository;
    incidents = new HashSet<>();
    patients = new HashSet<>();
    unitIds = new HashSet<>();
  }

  public void addIncident(final Incident incident) {
    Initializer.init(incident, Incident::getConcern, Incident::getUnits, Incident::getPatient);
    incidents.add(incident);
  }

  public void addPatient(final Patient patient) {
    Initializer.init(patient, Patient::getConcern, Patient::getIncidents);
    patients.add(patient);
  }

  public void addUnit(final int unitId) {
    unitIds.add(unitId);
  }

  public synchronized void sendNotifications() {
    List<Incident> incidentsToSend = copyCollectionAndClear(this.incidents);
    sendNotifications(incidentsToSend, Incident.class);

    List<Patient> patientsToSend = copyCollectionAndClear(this.patients);
    sendNotifications(patientsToSend, Patient.class);

    List<Integer> unitIdsToSend = copyCollectionAndClear(unitIds);
    List<Unit> unitsToSend = loadUnitsByIdList(unitIdsToSend);
    sendNotifications(unitsToSend, Unit.class);
  }

  @Transactional
  public List<Unit> loadUnitsByIdList(final List<Integer> unitIdsToSend) {
    List<Unit> units = unitRepository.findByIdIn(unitIdsToSend);
    units.forEach(unit -> Initializer.init(unit, Unit::getConcern, Unit::getIncidents, Unit::getIncidentStateChangedAtMap));
    return units;
  }

  private synchronized <T> void sendNotifications(final Collection<T> entities, final Class<T> type) {
    EntityEventHandler<T> entityEventHandler = entityEventFactory.getEntityEventHandler(type);
    entities.forEach(entityEventHandler::entityChanged);
  }

  private static <T> List<T> copyCollectionAndClear(final Collection<T> sourceCollection) {
    List<T> immutableList = ImmutableList.copyOf(sourceCollection);
    sourceCollection.clear();
    return immutableList;
  }
}
