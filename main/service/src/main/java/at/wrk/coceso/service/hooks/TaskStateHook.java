package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;

public interface TaskStateHook {

  public TaskState call(final Incident incident, final Unit unit, final TaskState state, final User user, final NotifyList notify);
}
