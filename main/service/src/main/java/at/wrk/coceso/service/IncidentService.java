package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.NotifyList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface IncidentService {

  Incident getById(int id);

  List<Incident> getAll(Concern concern);

  List<Incident> getAllRelevant(Concern concern);

  List<Incident> getAllActive(Concern concern);

  Map<Incident, TaskState> getRelated(Unit unit);

  Incident update(Incident incident, Concern concern, User user, NotifyList notify);

  Incident createHoldPosition(Point position, Unit unit, TaskState state, User user, NotifyList notify);

  void endTreatments(Patient patient, User user, NotifyList notify);

  Incident createTreatment(Patient patient, Unit group, User user, NotifyList notify);

  void assignPatient(int incidentId, int patientId, User user, NotifyList notify);

  void assignPatient(Incident incident, Patient patient, User user, NotifyList notify);
}
