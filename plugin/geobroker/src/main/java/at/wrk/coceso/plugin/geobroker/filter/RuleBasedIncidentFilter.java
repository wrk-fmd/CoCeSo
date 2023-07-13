package at.wrk.coceso.plugin.geobroker.filter;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleBasedIncidentFilter implements IncidentFilter {

    private static final List<IncidentType> SUPPORTED_INCIDENT_TYPES = List.of(
            IncidentType.Task,
            IncidentType.Transport,
            IncidentType.Relocation);
    private static final List<TaskState> RELEVANT_TASK_STATES = List.of(
            TaskState.Assigned,
            TaskState.ZBO,
            TaskState.ABO,
            TaskState.ZAO,
            TaskState.AAO);

    @Override
    public boolean isIncidentRelevantForGeoBroker(final CachedIncident incident) {
        return SUPPORTED_INCIDENT_TYPES.contains(incident.getIncidentType())
                && incident.getIncidentState() != IncidentState.Done
                && atLeastOneUnitAtIncidentOrOpen(incident);
    }

    private boolean atLeastOneUnitAtIncidentOrOpen(final CachedIncident incident) {
        return incident.getIncidentState() == IncidentState.Open
                || atLeastOneUnitAtIncident(incident);
    }

    private boolean atLeastOneUnitAtIncident(final CachedIncident incident) {
        return incident.getAssignedExternalUnitIds()
                .values()
                .stream()
                .anyMatch(RELEVANT_TASK_STATES::contains);
    }
}
