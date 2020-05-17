package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.service.JournalService;
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
    private JournalService journalService;

    @Override
    public TaskState call(final Incident incident, final Unit unit, final TaskState taskState) {
//        if (taskState != TaskState.Detached || incident.getState().isDone()) {
//            // Not detaching or already set to done
//            return taskState;
//        }

//        if (incident.getState().isOpen() && !incident.getType().isSingleUnit()) {
//            // Don't autoclose open incidents
//            return taskState;
//        }
//
//    if (incident.getUnits() != null && incident.getUnits().keySet().stream().anyMatch(u -> !u.equals(unit))) {
//      // Other units attached
//      return taskState;
//    }

        LOG.debug("Autoclosing incident {}", incident);

//        Changes changes = new Changes("incident");
//        changes.put("state", incident.getState(), IncidentState.Done);
//        incident.setState(IncidentState.Done);
//
//        incidentRepository.saveAndFlush(incident);
//        journalService.logAuto(JournalEntryType.INCIDENT_AUTO_STATE, incident.getConcern(), unit, incident, taskState, changes);

        if (incident.getUnits() != null) {
            // Remove current unit, if present (otherwise IncidentRemoveUnits will be executed)
            incident.getUnits().clear();
        }

//        notify.addIncident(incident);
//    hookService.callIncidentDone(incident, notify);

        return taskState;
    }
}
