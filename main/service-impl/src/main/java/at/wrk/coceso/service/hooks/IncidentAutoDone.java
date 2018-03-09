package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(5)
@Transactional
class IncidentAutoDone implements TaskStateHook {

  private final static Logger LOG = LoggerFactory.getLogger(IncidentAutoDone.class);

  @Autowired
  private IncidentRepository incidentRepository;

  @Autowired
  private HookService hookService;

  @Autowired
  private LogService logService;

  @Override
  public TaskState call(final Incident incident, final Unit unit, final TaskState taskState, final User user, final NotifyList notify) {
    if (taskState != TaskState.Detached || incident.getState().isDone()) {
      // Not detaching or already set to done
      return taskState;
    }

    if (incident.getState().isOpen() && !incident.getType().isSingleUnit()) {
      // Don't autoclose open incidents
      return taskState;
    }

    if (incident.getUnits() != null && incident.getUnits().keySet().stream().anyMatch(u -> !u.equals(unit))) {
      // Other units attached
      return taskState;
    }

    LOG.debug("{}: Autoclosing incident {}", user, incident);

    Changes changes = new Changes("incident");
    changes.put("state", incident.getState(), IncidentState.Done);
    incident.setState(IncidentState.Done);

    incidentRepository.saveAndFlush(incident);
    logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, incident.getConcern(), unit, incident, taskState, changes);

    if (incident.getUnits() != null) {
      // Remove current unit, if present (otherwise IncidentRemoveUnits will be executed)
      incident.getUnits().clear();
    }

    notify.add(incident);
    hookService.callIncidentDone(incident, user, notify);

    return taskState;
  }

}
