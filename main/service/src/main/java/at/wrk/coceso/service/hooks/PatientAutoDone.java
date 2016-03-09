package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class PatientAutoDone implements IncidentStateHook {

  private static final Logger LOG = LoggerFactory.getLogger(PatientAutoDone.class);

  @Autowired
  private PatientService patientService;

  @Override
  public void call(final Incident incident, final IncidentState state, final User user, final NotifyList notify) {
    if (incident.getType() == IncidentType.Transport && state == IncidentState.Done) {
      LOG.debug("{}: Autodischarging patient {} after transport", user, incident.getPatient());
      patientService.discharge(incident.getPatient(), user, notify);
    }
  }

}
