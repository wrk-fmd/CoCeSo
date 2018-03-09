package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.PatientServiceInternal;
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
  private PatientServiceInternal patientService;

  @Override
  public void call(final Incident incident, final User user, final NotifyList notify) {
    if (incident.getType() == IncidentType.Transport) {
      LOG.debug("{}: Autodischarging patient {} after transport", user, incident.getPatient());
      patientService.discharge(incident.getPatient(), user, notify);
    }
  }

}
