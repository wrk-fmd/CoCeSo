package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.UnitState;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class AvailableForDispatchingCalculator {
    private static final Set<IncidentType> BUSY_INCIDENT_TYPES = ImmutableSet.of(
            IncidentType.Task,
            IncidentType.Transport,
            IncidentType.Treatment,
            IncidentType.Standby);

    boolean isAvailableForDispatching(final Unit unit) {
        Set<Incident> assignedIncidents = Optional.ofNullable(unit.getIncidents()).map(Map::keySet).orElse(ImmutableSet.of());
        return unit.isPortable() && unit.getState() == UnitState.EB && hasNoBusyIncidentsAssigned(assignedIncidents);
    }

    private static boolean hasNoBusyIncidentsAssigned(final Set<Incident> assignedIncidents) {
        return assignedIncidents.stream().noneMatch(AvailableForDispatchingCalculator::isBusyIncident);
    }

    private static boolean isBusyIncident(final Incident incident) {
        return incident.getType() != null && BUSY_INCIDENT_TYPES.contains(incident.getType());
    }
}
