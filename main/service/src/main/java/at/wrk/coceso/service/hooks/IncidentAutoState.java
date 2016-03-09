package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class IncidentAutoState implements TaskStateHook {

  private final static Logger LOG = LoggerFactory.getLogger(IncidentAutoState.class);

  @Autowired
  private IncidentRepository incidentRepository;

  @Autowired
  private LogService logService;

  @Override
  public TaskState call(final Incident incident, final Unit unit, final TaskState taskState, final User user, final NotifyList notify) {
    IncidentState incidentState = null;

    switch (taskState) {
      case Assigned:
      case ZBO:
        if (incident.getUnits() == null || !incident.getUnits().entrySet().stream()
            .anyMatch(e -> !e.getKey().equals(unit) && e.getValue().isWorking())) {
          incidentState = IncidentState.Dispo;
        }
        break;
      case ABO:
      case ZAO:
      case AAO:
        incidentState = IncidentState.Working;
        break;
    }

    if (incidentState != null && incidentState != incident.getState()) {
      LOG.debug("{}: Autosetting state for incident {} to {}", user, incident, incidentState);

      Changes changes = new Changes("incident");
      changes.put("state", incident.getState(), incidentState);
      incident.setState(incidentState);

      incidentRepository.save(incident);
      logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, incident.getConcern(), unit, incident, taskState, changes);
      notify.add(incident);
    }

    return taskState;
  }

}
