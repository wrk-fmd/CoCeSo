package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
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
    public TaskState call(final Incident incident, final Unit unit, final TaskState taskState, final NotifyList notify) {
        incident.setStateChange();
        if (incident.getArrival() == null && taskState.isWorking()) {
            LOG.debug("Arrival timestamp of incident {} is not yet set and state changed to 'working'. Setting arrival timestamp.", incident);
            incident.setArrival();
        }

        return taskState;
    }
}
