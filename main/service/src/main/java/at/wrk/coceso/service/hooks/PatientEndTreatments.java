package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2)
@Transactional
public class PatientEndTreatments implements TaskStateHook, PatientDoneHook {

  private static final Logger LOG = LoggerFactory.getLogger(PatientEndTreatments.class);

  @Autowired
  private IncidentService incidentService;

  @Override
  public TaskState call(final Incident incident, final Unit unit, final TaskState state, final User user, final NotifyList notify) {
    if (incident.getType() == IncidentType.Transport && (state == TaskState.ZAO || state == TaskState.AAO)) {
      LOG.debug("{}: Ending treatments of patient {} on transport", user, incident.getPatient());
      incidentService.endTreatments(incident.getPatient(), user, notify);
    }
    return state;
  }

  @Override
  public void call(final Patient patient, final User user, final NotifyList notify) {
    LOG.debug("{}: Ending treatments of patient {} on discharge", user, patient);
    incidentService.endTreatments(patient, user, notify);
  }

}
