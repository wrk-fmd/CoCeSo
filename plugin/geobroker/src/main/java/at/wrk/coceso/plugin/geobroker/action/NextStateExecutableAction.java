package at.wrk.coceso.plugin.geobroker.action;

import at.wrk.coceso.plugin.geobroker.action.context.ActionRunnerContext;
import at.wrk.coceso.plugin.geobroker.action.factory.ActionUrlFactory;
import at.wrk.coceso.plugin.geobroker.contract.broker.OneTimeAction;
import at.wrk.coceso.service.TaskWriteService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.UUID;

public class NextStateExecutableAction implements ExecutableAction {
    private static final Logger LOG = LoggerFactory.getLogger(NextStateExecutableAction.class);

    public static final String ACTION_TYPE_STRING = "nextState";

    private final UUID actionId;
    private final NextStateUnitAction action;

    public NextStateExecutableAction(final UUID actionId, final NextStateUnitAction action) {
        this.actionId = actionId;
        this.action = action;
    }

    @Override
    public UUID getActionId() {
        return actionId;
    }

    @Override
    public OneTimeAction getOneTimeAction(final ActionUrlFactory urlFactory) {
        return new OneTimeAction(
                ACTION_TYPE_STRING,
                urlFactory.generateUrl(actionId),
                action.getGeoBrokerIncidentId(),
                action.getPlannedState().name().toUpperCase(Locale.ROOT));
    }

    @Override
    public void runAction(final ActionRunnerContext context) {
        TaskWriteService taskWriteService = context.getTaskWriteService();
        taskWriteService.changeState(action.getIncidentId(), action.getUnitId(), action.getPlannedState());
        LOG.info(
                "Unit #{} successfully updated its state of incident #{} to planned state {} by one-time-action '{}'.",
                action.getUnitId(),
                action.getIncidentId(),
                action.getPlannedState(),
                actionId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("actionId", actionId)
                .append("action", action)
                .toString();
    }
}
