package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
class CreateHoldPosition implements TaskStateHook {

    private final static Logger LOG = LoggerFactory.getLogger(CreateHoldPosition.class);

    @Autowired
    private IncidentServiceInternal incidentService;

    @Override
    public TaskState call(final Incident incident, final Unit unit, final TaskState taskState, final NotifyList notify) {
        if (taskState != TaskState.AAO) {
            return taskState;
        }

        if (incident.getType() == IncidentType.ToHome) {
            LOG.debug("Autodetaching ToHome {} of unit {} on AAO", incident, unit);
            // ToHome: Just detach, unit is marked as 'at home'
            return TaskState.Detached;
        }

        if (incident.getType() != IncidentType.Relocation) {
            return taskState;
        }

        // If Relocation goes to unit.home -> just detach, so unit is marked as 'at Home'
        if (incident.hasAo() && !Point.infoEquals(incident.getAo(), unit.getHome())) {
            LOG.debug("Creating HoldPosition for unit {} on AAO. Relocation {} will be detached.", unit, incident);
            incidentService.createHoldPosition(incident.getAo(), unit, TaskState.AAO, notify);
        } else {
            LOG.debug("Relocation task '{}' of unit '{}' had 'Home' as target. Unit is automatically detached to set it to 'at home'.", incident, unit);
        }

        return TaskState.Detached;
    }
}
