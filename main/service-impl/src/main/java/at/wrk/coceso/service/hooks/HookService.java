package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HookService {

    @Autowired
    private List<TaskStateHook> taskStateHooks;
    @Autowired
    private List<IncidentDoneHook> incidentStateHooks;
    @Autowired
    private List<PatientDoneHook> patientDoneHooks;

    public TaskState callTaskStateChanged(final Incident incident, final Unit unit, final TaskState state, final User user, final NotifyList notify) {
        TaskState calculatedTaskState = state;
        for (TaskStateHook taskStateHook : taskStateHooks) {
            calculatedTaskState = taskStateHook.call(incident, unit, state, user, notify);
        }

        return calculatedTaskState;
    }

    public void callIncidentDone(final Incident incident, final User user, final NotifyList notify) {
        incidentStateHooks.forEach(h -> h.call(incident, user, notify));
    }

    public void callPatientDone(final Patient patient, final User user, final NotifyList notify) {
        patientDoneHooks.forEach(h -> h.call(patient, user, notify));
    }

}
