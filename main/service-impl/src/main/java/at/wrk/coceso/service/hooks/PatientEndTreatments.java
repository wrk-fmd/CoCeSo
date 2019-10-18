package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2)
@Transactional
class PatientEndTreatments implements TaskStateHook, PatientDoneHook {

  private static final Logger LOG = LoggerFactory.getLogger(PatientEndTreatments.class);

  @Autowired
  private IncidentServiceInternal incidentService;

  @Override
  public TaskState call(final Incident incident, final Unit unit, final TaskState state, final NotifyList notify) {
    if (incident.getType() == IncidentType.Transport && (state == TaskState.ZAO || state == TaskState.AAO)) {
      LOG.debug("Ending treatments of patient {} due to starting transport by unit {}.", incident.getPatient(), unit);
      incidentService.endTreatments(incident.getPatient(), notify);
    }

    return state;
  }

  @Override
  public void call(final Patient patient, final NotifyList notify) {
    LOG.debug("Ending treatments of patient {} on discharge", patient);
    incidentService.endTreatments(patient, notify);
  }

}
