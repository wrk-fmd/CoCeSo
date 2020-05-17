package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.service.JournalService;
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
    private JournalService journalService;

    @Override
    public void call(final Incident incident) {
//    if (incident.getUnits() != null && !incident.getUnits().isEmpty()) {
//      ImmutableSet.copyOf(incident.getUnits().keySet())
//          .forEach(unit -> {
//            LOG.debug("Auto-detach unit #{} from incident #{}", unit.getId(), incident.getId());
//            logService.logAuto(LogEntryType.UNIT_AUTO_DETACH, unit.getConcern(), unit, incident, TaskState.Detached);
//            unit.removeIncident(incident);
//            notify.addUnit(unit.getId());
//          });
//
//      incidentRepository.saveAndFlush(incident);
//      notify.addIncident(incident);
//    }
    }
}
