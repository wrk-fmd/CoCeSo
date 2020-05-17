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

@Component
@Order(4)
class IncidentAutoState implements TaskStateHook {

    private final static Logger LOG = LoggerFactory.getLogger(IncidentAutoState.class);

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private JournalService journalService;

    @Override
    public TaskState call(final Incident incident, final Unit unit, final TaskState taskState) {
//        if (taskState != TaskState.Detached
//                && incident.getState() != IncidentState.Demand
//                && incident.getState() != IncidentState.InProgress) {
//            LOG.debug(
//                    "Task state changed to not-Detached, and incident state is not in 'Demand' or 'InProgress'. Auto-set state for incident {} to 'InProgress'",
//                    incident);
//
//            Changes changes = new Changes("incident");
//            changes.put("state", incident.getState(), IncidentState.InProgress);
//            incident.setState(IncidentState.InProgress);
//
//            incidentRepository.saveAndFlush(incident);
//            journalService.logAuto(JournalEntryType.INCIDENT_AUTO_STATE, incident.getConcern(), unit, incident, taskState, changes);
//        }
//
        return taskState;
    }
}
