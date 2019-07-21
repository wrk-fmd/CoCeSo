package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
class StateChangeTimestampHook implements TaskStateHook {
  private static final Logger LOG = LoggerFactory.getLogger(StateChangeTimestampHook.class);

  @Override
  public TaskState call(final Incident incident, final Unit unit, final TaskState taskState, final User user, final NotifyList notify) {
    incident.setStateChange();
    if (incident.getArrival() == null && taskState.isWorking()) {
      LOG.debug("{}: Arrival timestamp is not set yet and state changed to 'working'. Setting arrival timestamp.", user);
      incident.setArrival();
    }

    return taskState;
  }

}
