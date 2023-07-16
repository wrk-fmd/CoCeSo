package at.wrk.coceso.plugin.geobroker.action.factory;

import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.action.NextStateUnitAction;
import at.wrk.coceso.plugin.geobroker.action.UnitAction;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UnitActionFactory {
    private static final Map<TaskState, TaskState> TASK_STATES_OF_TASK_OR_TRANSPORT = ImmutableMap.of(
            TaskState.Assigned, TaskState.ZBO,
            TaskState.ZBO, TaskState.ABO,
            TaskState.ABO, TaskState.ZAO,
            TaskState.ZAO, TaskState.AAO,
            TaskState.AAO, TaskState.Detached
    );

    private static final Map<TaskState, TaskState> TASK_STATES_OF_RELOCATION = ImmutableMap.of(
            TaskState.Assigned, TaskState.ZAO,
            TaskState.ZAO, TaskState.AAO,
            TaskState.AAO, TaskState.Detached
    );
    private final ActionUrlFactory actionUrlFactory;

    @Autowired
    public UnitActionFactory(final ActionUrlFactory actionUrlFactory) {
        this.actionUrlFactory = actionUrlFactory;
    }

    public List<UnitAction> buildUnitActions(final CachedUnit cachedUnit, final List<CachedIncident> assignedIncidents) {
        List<UnitAction> actions;

        if (actionUrlFactory.isOneTimeActionFeatureActive()) {
            actions = assignedIncidents
                    .stream()
                    .map(incident -> buildNextStateAction(cachedUnit, incident))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(ImmutableList.toImmutableList());
        } else {
            actions = ImmutableList.of();
        }

        return actions;
    }

    private Optional<NextStateUnitAction> buildNextStateAction(final CachedUnit cachedUnit, final CachedIncident incident) {
        TaskState currentState = cachedUnit.getIncidentsWithState().get(incident.getGeoBrokerIncidentId());
        Optional<TaskState> plannedState = calculatePlannedState(incident.getIncidentType(), currentState, incident.getDestination() != null);

        return plannedState
                .map(state -> new NextStateUnitAction(cachedUnit.getUnitId(), incident.getIncidentId(), incident.getGeoBrokerIncidentId(), state));
    }

    private Optional<TaskState> calculatePlannedState(final IncidentType incidentType, final TaskState currentState, final boolean isAoPresent) {
        Optional<TaskState> plannedState;

        switch (incidentType) {
            case Task:
            case Transport:
                plannedState = Optional.ofNullable(TASK_STATES_OF_TASK_OR_TRANSPORT.get(currentState))
                        .filter(newState -> checkIfAoIsPresentForZaoState(isAoPresent, newState));
                break;
            case Relocation:
                plannedState = Optional.ofNullable(TASK_STATES_OF_RELOCATION.get(currentState));
                break;
            default:
                plannedState = Optional.empty();
                break;
        }

        return plannedState;
    }

    private boolean checkIfAoIsPresentForZaoState(final boolean isAoPresent, final TaskState newState) {
        return newState != TaskState.ZAO || isAoPresent;
    }
}
