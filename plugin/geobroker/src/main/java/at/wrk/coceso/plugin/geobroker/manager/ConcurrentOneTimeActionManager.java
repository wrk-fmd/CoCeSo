package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.plugin.geobroker.action.ExecutableAction;
import at.wrk.coceso.plugin.geobroker.action.UnitAction;
import at.wrk.coceso.plugin.geobroker.action.context.ActionRunnerContext;
import at.wrk.coceso.plugin.geobroker.action.factory.ActionUrlFactory;
import at.wrk.coceso.plugin.geobroker.contract.broker.OneTimeAction;
import at.wrk.coceso.plugin.geobroker.contract.ota.api.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ThreadSafe
public class ConcurrentOneTimeActionManager implements OneTimeActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentOneTimeActionManager.class);

    private final Map<String, List<UnitAction>> registeredUnitActions;
    private final Map<UnitAction, ExecutableAction> executableActionMap;
    private final Map<UUID, ExecutableAction> executableActionCache;
    private final ActionUrlFactory actionUrlFactory;
    private final ActionRunnerContext actionRunnerContext;

    @Autowired
    public ConcurrentOneTimeActionManager(final ActionUrlFactory actionUrlFactory, final ActionRunnerContext actionRunnerContext) {
        this.actionUrlFactory = actionUrlFactory;
        this.actionRunnerContext = actionRunnerContext;
        registeredUnitActions = new ConcurrentHashMap<>();
        executableActionMap = new ConcurrentHashMap<>();
        executableActionCache = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized List<OneTimeAction> registerActions(final String geoBrokerUnitId, final List<UnitAction> updatedActions) {
        List<UnitAction> oldActions = registeredUnitActions.getOrDefault(geoBrokerUnitId, List.of());
        Stream<UnitAction> removedActions = oldActions
                .stream()
                .filter(action -> !updatedActions.contains(action));
        Stream<UnitAction> addedActions = updatedActions
                .stream()
                .filter(action -> !oldActions.contains(action));
        registeredUnitActions.put(geoBrokerUnitId, updatedActions);

        AtomicInteger amountOfRemovedActions = new AtomicInteger();
        removedActions
                .peek(x -> amountOfRemovedActions.getAndIncrement())
                .forEach(action -> cleanupUnitAction(geoBrokerUnitId, action));
        AtomicInteger amountOfAddedActions = new AtomicInteger();
        addedActions
                .peek(x -> amountOfAddedActions.getAndIncrement())
                .forEach(action -> buildExecutableAction(geoBrokerUnitId, action));

        LOG.debug(
                "Update of geobroker unit '{}' created {} new one-time-actions and removed {} existing one-time-actions. Providing now a total of {} actions.",
                geoBrokerUnitId,
                amountOfAddedActions,
                amountOfRemovedActions,
                updatedActions.size());

        return updatedActions
                .stream()
                .map(executableActionMap::get)
                .map(action -> action.getOneTimeAction(actionUrlFactory))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ResultCode executeAction(final UUID actionId) {
        ExecutableAction action = executableActionCache.remove(actionId);

        ResultCode code;

        if (action == null) {
            LOG.debug("Executable action with actionId '{}' was not found in cache.", actionId);
            code = ResultCode.ACTION_ID_OUTDATED;
        } else {
            try {
                LOG.debug("Running one-time-action: {}", action);
                action.runAction(actionRunnerContext);
                code = ResultCode.SUCCESS;
            } catch (Exception e) {
                LOG.warn("Failed to run one-time-action. Error: {}", e.getMessage());
                LOG.info("Underlying exception:", e);
                code = ResultCode.OPERATION_FAILED;
            }
        }

        return code;
    }

    private void buildExecutableAction(final String geoBrokerUnitId, final UnitAction action) {
        LOG.debug("Adding new action for unit '{}': {}", geoBrokerUnitId, action);
        ExecutableAction executableAction = action.buildExecutableAction();
        executableActionCache.put(executableAction.getActionId(), executableAction);
        executableActionMap.put(action, executableAction);
    }

    private void cleanupUnitAction(final String geoBrokerUnitId, final UnitAction action) {
        ExecutableAction removedAction = executableActionMap.remove(action);
        if (removedAction != null) {
            UUID actionId = removedAction.getActionId();
            LOG.debug("Action '{}' of unit '{}' was removed.", actionId, geoBrokerUnitId);
            executableActionCache.remove(actionId);
        } else {
            LOG.warn("Action does not have an associated executable action to remove: {}", action);
        }
    }
}
