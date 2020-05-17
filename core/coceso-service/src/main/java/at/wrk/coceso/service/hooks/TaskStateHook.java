package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;

interface TaskStateHook {

    TaskState call(Incident incident, Unit unit, TaskState state);
}
