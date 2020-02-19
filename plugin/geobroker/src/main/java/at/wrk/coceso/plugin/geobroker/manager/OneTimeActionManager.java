package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.plugin.geobroker.action.UnitAction;
import at.wrk.coceso.plugin.geobroker.contract.broker.OneTimeAction;
import at.wrk.coceso.plugin.geobroker.contract.ota.api.ResultCode;

import java.util.List;
import java.util.UUID;

public interface OneTimeActionManager {
    /**
     * Registers the given actions. For each action provided in the parameter a value is returned in the result collection. All actions which are no longer
     * provided in the parameter and were provided previously are invalidated. If an action was already provided before, the resulting action will contain the
     * same URL.
     */
    List<OneTimeAction> registerActions(final String geoBrokerUnitId, final List<UnitAction> actions);

    ResultCode executeAction(final UUID actionId);
}
