package at.wrk.coceso.service.impl;

import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.entity.point.UnitSupplier;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.importer.UnitImporter;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import at.wrk.coceso.service.internal.TaskServiceInternal;
import at.wrk.coceso.service.internal.UnitServiceInternal;
import at.wrk.coceso.utils.AuthenicatedUserProvider;
import at.wrk.coceso.utils.Initializer;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
class UnitServiceImpl implements UnitServiceInternal, UnitSupplier {

  private final static Logger LOG = LoggerFactory.getLogger(UnitServiceImpl.class);

  @Autowired
  private UnitRepository unitRepository;

  @Autowired
  private LogService logService;

  @Autowired
  private IncidentServiceInternal incidentService;

  @Autowired
  private TaskServiceInternal taskService;

  @Autowired
  private UserService userService;

  @Autowired
  private UnitImporter unitImporter;

  private final AuthenicatedUserProvider authenicatedUserProvider;

  @Autowired
  UnitServiceImpl(final AuthenicatedUserProvider authenicatedUserProvider) {
    this.authenicatedUserProvider = authenicatedUserProvider;
  }

  @Override
  public Unit getById(int id) {
    // See https://stackoverflow.com/a/53852146. 'findOne()' uses cached entities with outdated data.
    return unitRepository.findByIdIn(ImmutableList.of(id)).stream().findFirst().orElse(null);
  }

  @Override
  public Unit getTreatmentByCall(String call, Concern concern) {
    return (call == null || concern == null) ? null : unitRepository.findFirstByCallIgnoreCaseAndConcernAndTypeIn(call, concern, UnitType.treatmentTypes);
  }

  @Override
  public List<Unit> getAll(Concern concern) {
    return unitRepository.findByConcern(concern);
  }

  @Override
  public List<Unit> getAllSorted(Concern concern) {
    return unitRepository.findByConcern(concern, Sort.by(Sort.Direction.ASC, "id"));
  }

  @Override
  public List<Unit> getByUser(User user, Collection<UnitType> types) {
    return user == null ? null : unitRepository.findByUser(user, types);
  }

  @Override
  public List<Unit> getByConcernUser(Concern concern, User user) {
    return unitRepository.findByConcernUser(concern, user);
  }

  @Override
  public Map<Unit, TaskState> getRelated(Incident incident) {
    return unitRepository.findByIdIn(unitRepository.findRelated(incident)).stream().collect(Collectors.toMap(
        Function.identity(), u -> u.getIncidents().getOrDefault(incident, TaskState.Detached)));
  }

  @Override
  public Unit updateMain(final Unit unit, final NotifyList notify) {
    LOG.debug("{}: Triggered update of unit {}", authenicatedUserProvider.getAuthenticatedUser(), unit);
    if (unit.getId() == null) {
      LOG.warn("{}: Tried to create unit via 'update' method. Operation is rejected.", authenicatedUserProvider.getAuthenticatedUser());
      throw new ErrorsException(Errors.UnitCreateNotAllowed);
    }

    Map<Incident, TaskState> incidents = unit.getIncidents();

    Unit save = getById(unit.getId());
    if (save == null) {
      // Unit missing, should be checked by validator!
      throw new ErrorsException(Errors.EntityMissing);
    }

    if (save.getConcern().isClosed()) {
      LOG.warn("{}: Tried to update unit {} in closed concern", authenicatedUserProvider.getAuthenticatedUser(), unit);
      throw new ErrorsException(Errors.ConcernClosed);
    }

    // Set updateable properties
    Changes changes = new Changes("unit");
    if (unit.getState() != null && unit.getState() != save.getState()) {
      changes.put("state", save.getState(), unit.getState());
      save.setState(unit.getState());
    }

    if (unit.getInfo() != null && !unit.getInfo().equals(save.getInfo())) {
      changes.put("info", save.getInfo(), unit.getInfo());
      save.setInfo(unit.getInfo());
    }

    Point position = Point.create(unit.getPosition(), save.getConcern());
    if (unit.getPosition() != null && !Point.infoEquals(position, save.getPosition())) {
      changes.put("position", Point.toStringOrNull(save.getPosition()), Point.toStringOrNull(position));
      save.setPosition(position);
    }

    final Unit updatedUnit = unitRepository.saveAndFlush(save);
    logService.logAuto(LogEntryType.UNIT_UPDATE, updatedUnit.getConcern(), updatedUnit, null, changes);
    notify.addUnit(updatedUnit.getId());

    if (incidents != null) {
      incidents.forEach((incident, state) -> taskService.changeState(incidentService.getById(incident.getId()), updatedUnit, state, notify));
    }

    return updatedUnit;
  }

  @Override
  public Unit updateEdit(final Unit unit, final Concern concern, final NotifyList notify) {
    Changes changes = new Changes("unit");
    Unit save;
    if (unit.getId() == null) {
      LOG.info("{}: Triggered unit create: {}", authenicatedUserProvider.getAuthenticatedUser(), unit);

      if (Concern.isClosedOrNull(concern)) {
        LOG.warn("{}: Tried to create unit {} in closed concern.", authenicatedUserProvider.getAuthenticatedUser(), unit);
        throw new ErrorsException(Errors.ConcernClosed);
      }

      save = new Unit();

      // Set updated properties
      save.setConcern(concern);

      changes.put("call", null, unit.getCall());
      save.setCall(unit.getCall());

      if (StringUtils.isNotBlank(unit.getAni())) {
        changes.put("ani", null, unit.getAni());
        save.setAni(unit.getAni());
      }

      if (StringUtils.isNotBlank(unit.getInfo())) {
        changes.put("info", null, unit.getInfo());
        save.setInfo(unit.getInfo());
      }

      // Using null for the concern prevents a UnitPoint being created. Maybe make that more explicit?
      Point home = Point.create(unit.getHome(), null);
      if (!Point.isEmpty(home)) {
        changes.put("home", null, Point.toStringOrNull(home));
        save.setHome(home);
      }

      if (unit.getType() != null) {
        changes.put("type", null, unit.getType());
        save.setType(unit.getType());
      }

      changes.put("withDoc", null, unit.isWithDoc());
      save.setWithDoc(unit.isWithDoc());

      changes.put("portable", null, unit.isPortable());
      save.setPortable(unit.isPortable());

      changes.put("transportVehicle", null, unit.isTransportVehicle());
      save.setTransportVehicle(unit.isTransportVehicle());

      save.setLocked(false);
    } else {
      LOG.info("{}: Triggered update of unit: {}", authenicatedUserProvider, unit);

      save = getById(unit.getId());
      if (save == null) {
        // Unit missing, should be checked by validator!
        throw new ErrorsException(Errors.EntityMissing);
      }

      if (save.getConcern().isClosed()) {
        LOG.warn("{}: Tried to update unit {} in closed concern", authenicatedUserProvider.getAuthenticatedUser(), unit);
        throw new ErrorsException(Errors.ConcernClosed);
      }

      // Set updateable properties
      if (unit.getCall() != null && !unit.getCall().equals(save.getCall())) {
        changes.put("call", save.getCall(), unit.getCall());
        save.setCall(unit.getCall());
      }

      if (!Objects.equals(save.getAni(), unit.getAni())) {
        changes.put("ani", save.getAni(), unit.getAni());
        save.setAni(unit.getAni());
      }

      if (!Objects.equals(save.getInfo(), unit.getInfo())) {
        changes.put("info", save.getInfo(), unit.getInfo());
        save.setInfo(unit.getInfo());
      }

      Point home = Point.create(unit.getHome(), null);
      if (!Point.infoEquals(home, save.getHome())) {
        changes.put("home", Point.toStringOrNull(save.getHome()), Point.toStringOrNull(home));
        save.setHome(home);
      }

      if (unit.getType() != save.getType()) {
        changes.put("type", save.getType(), unit.getType());
        save.setType(unit.getType());
      }

      if (unit.isWithDoc() != save.isWithDoc()) {
        changes.put("withDoc", save.isWithDoc(), unit.isWithDoc());
        save.setWithDoc(unit.isWithDoc());
      }

      if (unit.isPortable() != save.isPortable()) {
        changes.put("portable", save.isPortable(), unit.isPortable());
        save.setPortable(unit.isPortable());
      }

      if (unit.isTransportVehicle() != save.isTransportVehicle()) {
        changes.put("transportVehicle", save.isTransportVehicle(), unit.isTransportVehicle());
        save.setTransportVehicle(unit.isTransportVehicle());
      }
    }

    if (unit.getSection() == null || !save.getConcern().containsSection(unit.getSection())) {
      save.setSection(null);
    } else {
      save.setSection(unit.getSection());
    }

    Unit updatedUnit = unitRepository.saveAndFlush(save);
    logService.logAuto(LogEntryType.UNIT_CREATE, updatedUnit.getConcern(), updatedUnit, null, changes);

    notify.addUnit(updatedUnit.getId());

    return updatedUnit;
  }

  @Override
  public List<Integer> batchCreate(final BatchUnits batch, final Concern concern, final NotifyList notify) {
    List<Integer> ids = new LinkedList<>();

    Unit unit = new Unit();
    unit.setId(null);
    unit.setPortable(batch.isPortable());
    unit.setWithDoc(batch.isWithDoc());
    unit.setTransportVehicle(batch.isTransportVehicle());
    unit.setHome(batch.getHome());

    for (int i = batch.getFrom(); i <= batch.getTo(); i++) {
      unit.setCall(batch.getCall() + i);
      Unit added = updateEdit(unit, concern, notify);
      ids.add(added.getId());
    }

    return ids;
  }

  @Override
  public Unit doRemove(final int unitId) {
    Unit unit = Initializer.init(getById(unitId), Unit::getConcern);
    if (unit == null) {
      LOG.info("{}: Tried to remove non-existing Unit #{}", authenicatedUserProvider.getAuthenticatedUser(), unitId);
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.isLocked()) {
      LOG.warn("{}: Tried to remove non-deletable Unit #{}", authenicatedUserProvider.getAuthenticatedUser(), unit.getId());
      throw new ErrorsException(Errors.UnitLocked);
    }

    logService.updateForRemoval(unit);
    unitRepository.delete(unit);
    return unit;
  }

  @Override
  public void sendHome(final int unitId, final NotifyList notify) {
    Unit unit = getById(unitId);
    if (unit == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    if (unit.getIncidents().keySet().stream()
        .anyMatch(i -> (i.getType() != IncidentType.Standby && i.getType() != IncidentType.HoldPosition))) {
      throw new ErrorsException(Errors.IncidentNotAllowed);
    }

    Incident inc = new Incident();
    inc.setType(IncidentType.ToHome);
    inc.setBo(unit.getPosition());
    setPropertiesAndSave(notify, unit, inc);
  }

  @Override
  public void holdPosition(final int unitId, final NotifyList notify) {
    Unit unit = getById(unitId);
    if (unit == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }
    if (!unit.getIncidents().isEmpty()) {
      throw new ErrorsException(Errors.IncidentNotAllowed);
    }

    incidentService.createHoldPosition(unit.getPosition(), unit, TaskState.Assigned, notify);
  }

  @Override
  public void standby(final int unitId, final NotifyList notify) {
    Unit unit = getById(unitId);
    if (unit == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    if (unit.getIncidents().keySet().stream()
        .anyMatch(i -> (i.getType() != IncidentType.ToHome && i.getType() != IncidentType.HoldPosition))) {
      throw new ErrorsException(Errors.IncidentNotAllowed);
    }

    Incident inc = new Incident();
    inc.setType(IncidentType.Standby);
    setPropertiesAndSave(notify, unit, inc);
  }

  @Override
  public void removeCrew(final int unitId, final int userId, final NotifyList notify) {
    Unit unit = getById(unitId);
    User user = userService.getById(userId);
    if (unit == null || user == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernMissing);
    }

    unit.removeCrew(user);
    unitRepository.saveAndFlush(unit);
    notify.addUnit(unit.getId());
  }

  @Override
  public void addCrew(final int unitId, final int userId, final NotifyList notify) {
    Unit unit = getById(unitId);
    User user = userService.getById(userId);
    if (unit == null || user == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernMissing);
    }

    unit.addCrew(user);
    unitRepository.saveAndFlush(unit);
    notify.addUnit(unit.getId());
  }

  @Override
  public int importUnits(final String data, final Concern concern, final NotifyList notify) {
    LOG.info("{}: started import of units", authenicatedUserProvider.getAuthenticatedUser());

    Map<Unit, Changes> units = unitImporter.importUnits(data, concern, getAll(concern));
    units.forEach((unit, changes) -> {
      Unit savedUnit = unitRepository.saveAndFlush(unit);
      logService.logAuto(LogEntryType.UNIT_CREATE, savedUnit.getConcern(), savedUnit, null, changes);
      notify.addUnit(savedUnit.getId());
    });
    return units.size();
  }


  private void setPropertiesAndSave(final NotifyList notify, final Unit unit, final Incident inc) {
    inc.setState(IncidentState.InProgress);
    Optional.ofNullable(authenicatedUserProvider.getAuthenticatedUser())
            .map(AuthenticatedUser::getUsername)
            .ifPresent(inc::setCaller);
    inc.setAo(unit.getHome());

    Incident savedIncident = incidentService.update(inc, unit.getConcern(), notify);
    taskService.changeState(savedIncident, unit, TaskState.Assigned, notify);
  }
}
