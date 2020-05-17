package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
class PatientAutoDone implements IncidentDoneHook {

    private static final Logger LOG = LoggerFactory.getLogger(PatientAutoDone.class);

    @Autowired
    private PatientService patientService;

    @Override
    public void call(final Incident incident) {
        if (incident.getType() == IncidentType.Transport) {
            LOG.debug("Auto-discharging patient '{}' after transport of incident '{}'.", incident.getPatient(), incident);
            patientService.discharge(incident.getPatient());
        }
    }
}
