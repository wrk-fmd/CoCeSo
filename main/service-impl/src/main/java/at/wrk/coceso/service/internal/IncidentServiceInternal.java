package at.wrk.coceso.service.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.IncidentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface IncidentServiceInternal extends IncidentService {

  Incident update(Incident incident, Concern concern, User user, NotifyList notify);

  Incident createHoldPosition(Point position, Unit unit, TaskState state, User user, NotifyList notify);

  void endTreatments(Patient patient, User user, NotifyList notify);

  Incident createTreatment(Patient patient, Unit group, User user, NotifyList notify);

  void assignPatient(int incidentId, int patientId, User user, NotifyList notify);

  void assignPatient(Incident incident, Patient patient, User user, NotifyList notify);
}
