package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.hooks.HookService;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.internal.TaskServiceInternal;

@Service
@Transactional
class TaskServiceImpl implements TaskServiceInternal {

  private final static Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

  @Autowired
  private UnitRepository unitRepository;

  @Autowired
  private IncidentService incidentService;

  @Autowired
  private UnitService unitService;

  @Autowired
  private HookService hookService;

  @Autowired
  private LogService logService;

  @Override
  public synchronized void changeState(int incident_id, int unit_id, TaskState state, User user, NotifyList notify) {
    Incident i = incidentService.getById(incident_id);
    Unit u = unitService.getById(unit_id);
    changeState(i, u, state, user, notify);
  }

  @Override
  public synchronized void changeState(Incident i, Unit u, TaskState state, User user, NotifyList notify) {
    LOG.debug("{}: Trying to update unit {} and incident {} to '{}'", user, u, i, state);

    if (i == null || u == null) {
      LOG.info("{}: Unit/incident not found", user);
      throw new ErrorsException(Errors.EntityMissing);
    }

    if (!i.getConcern().equals(u.getConcern())) {
      LOG.info("{}: Combination Unit {}/incident {} in different concerns", user, u, i);
      throw new ErrorsException(Errors.ConcernMismatch);
    }

    if (i.getConcern().isClosed()) {
      LOG.info("{}: Tried to change TaskState in closed concern", user);
      throw new ErrorsException(Errors.ConcernClosed);
    }

    if (i.getType() == IncidentType.Treatment) {
      LOG.info("{}: Tried to change TaskState for treatment incident", user);
      throw new ErrorsException(Errors.IncidentNotAllowed);
    }

    if (!i.getType().isPossibleState(state)) {
      LOG.warn("{}: TaskService.changeState(): New state not possible for Unit {}/incident {}", user, u, i);
      throw new ErrorsException(Errors.ImpossibleTaskState);
    }

    if (i.getUnits() == null || i.getUnits().get(u) == null) {
      // Unit was not assigned previously
      u = assign(i, u, state, user, notify);
    } else {
      // Unit was already assigned
      u = setState(i, u, state, user, notify);
    }

    if (u != null) {
      u = unitRepository.saveAndFlush(u);

      notify.add(i);
      notify.add(u);
    }
  }

  @Override
  public void uncheckedChangeState(Incident i, Unit u, TaskState state, User user, NotifyList notify) {
    if (state == TaskState.Detached) {
      u.removeIncident(i);
    } else {
      u.addIncident(i, state);
    }
    u = unitRepository.saveAndFlush(u);
    notify.add(i);
    notify.add(u);
  }

  private Unit assign(Incident i, final Unit u, TaskState state, final User user, final NotifyList notify) {
    if (state == TaskState.Detached) {
      // We are detaching, but unit isn't assigned anyway: Nothing to do anymore
      return null;
    }

    // HoldPosition and Standby can't be assigned to multiple Units
    if (i.getType().isSingleUnit() && i.getUnits() != null && !i.getUnits().isEmpty()) {
      LOG.debug("{}: Tried to assign multiple units ({}) to single unit incident {}", user, u, i);
      throw new ErrorsException(Errors.MultipleUnits);
    }

    // Auto-Detach unit from all SingleUnit Incidents and Relocation
    u.getIncidents().keySet().stream()
        .filter(inc -> (inc.getType().isSingleUnit() || inc.getType() == IncidentType.Relocation))
        .forEach(inc -> {
          LOG.debug("{}: Auto-detach unit #{}, incident #{}", user, u.getId(), inc.getId());
          logService.logAuto(user, LogEntryType.UNIT_AUTO_DETACH, u.getConcern(), u, inc, TaskState.Detached);

          hookService.callTaskStateChanged(inc, u, TaskState.Detached, user, notify);
          u.removeIncident(inc);

          notify.add(inc);
        });

    state = hookService.callTaskStateChanged(i, u, state, user, notify);

    // Add incident to unit's task list
    u.addIncident(i, state);

    logService.logAuto(user, LogEntryType.UNIT_ASSIGN, u.getConcern(), u, i, state);

    return u;
  }

  private Unit setState(Incident i, Unit u, TaskState state, User user, NotifyList notify) {
    logService.logAuto(user, state == TaskState.Detached ? LogEntryType.UNIT_DETACH : LogEntryType.TASKSTATE_CHANGED, i.getConcern(), u, i, state);

    // Call additional hooks
    state = hookService.callTaskStateChanged(i, u, state, user, notify);

    if (state == TaskState.Detached) {
      LOG.debug("{}: Detaching unit {} from incident {}", user, u, i);
      u.removeIncident(i);
    } else {
      // Only save if not already done by assignUnit
      LOG.debug("{}: Updating TaskState for unit {}/incident {} to {}", user, u, i, state);
      u.addIncident(i, state);
    }

    return u;
  }

}
