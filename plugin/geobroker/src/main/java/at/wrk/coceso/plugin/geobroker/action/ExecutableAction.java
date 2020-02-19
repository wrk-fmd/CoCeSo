package at.wrk.coceso.plugin.geobroker.action;

import at.wrk.coceso.plugin.geobroker.action.context.ActionRunnerContext;
import at.wrk.coceso.plugin.geobroker.action.factory.ActionUrlFactory;
import at.wrk.coceso.plugin.geobroker.contract.broker.OneTimeAction;

import java.util.UUID;

public interface ExecutableAction {
    UUID getActionId();

    OneTimeAction getOneTimeAction(ActionUrlFactory urlFactory);

    void runAction(ActionRunnerContext context);
}
