package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HookService {

  @Autowired
  private List<TaskStateHook> taskStateHooks;

  @Autowired
  private List<IncidentStateHook> incidentStateHooks;

  @Autowired
  private List<PatientDoneHook> patientDoneHooks;

  public TaskState callTaskStateChanged(Incident incident, Unit unit, TaskState state, User user, NotifyList notify) {
    for (TaskStateHook taskStateHook : taskStateHooks) {
      state = taskStateHook.call(incident, unit, state, user, notify);
    }
    return state;
  }

  public void callIncidentDone(Incident incident, IncidentState state, User user, NotifyList notify) {
    incidentStateHooks.forEach(h -> h.call(incident, state, user, notify));
  }

  public void callPatientDone(Patient patient, User user, NotifyList notify) {
    patientDoneHooks.forEach(h -> h.call(patient, user, notify));
  }

}
