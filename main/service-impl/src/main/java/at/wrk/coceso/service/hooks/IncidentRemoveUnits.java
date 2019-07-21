package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.service.LogService;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
class IncidentRemoveUnits implements IncidentDoneHook {

  private final static Logger LOG = LoggerFactory.getLogger(IncidentRemoveUnits.class);

  @Autowired
  private IncidentRepository incidentRepository;

  @Autowired
  private LogService logService;

  @Override
  public void call(final Incident incident, final User user, final NotifyList notify) {
    if (incident.getUnits() != null && !incident.getUnits().isEmpty()) {
      ImmutableSet.copyOf(incident.getUnits().keySet())
          .forEach(unit -> {
            LOG.debug("{}: Auto-detach unit #{}, incident #{}", user, unit.getId(), incident.getId());
            logService.logAuto(user, LogEntryType.UNIT_AUTO_DETACH, unit.getConcern(), unit, incident, TaskState.Detached);
            unit.removeIncident(incident);
            notify.add(unit);
          });

      incidentRepository.saveAndFlush(incident);
      notify.add(incident);
    }
  }

}
