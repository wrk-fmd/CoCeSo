package at.wrk.coceso.plugin.geobroker.action.context;

import at.wrk.coceso.service.TaskWriteService;

/**
 * Provides all injectable dependencies to the executable action instances.
 */
public interface ActionRunnerContext {
    TaskWriteService getTaskWriteService();
}
