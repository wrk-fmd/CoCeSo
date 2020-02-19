package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerPoint;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class TargetPointExtractor {

    private static final ImmutableSet<IncidentType> RELOCATION_INCIDENT_TYPES = ImmutableSet.of(IncidentType.ToHome, IncidentType.Relocation);

    @Nullable
    public GeoBrokerPoint getTargetPoint(final CachedIncident incident, final TaskState taskState) {
        GeoBrokerPoint targetPoint;
        if (RELOCATION_INCIDENT_TYPES.contains(incident.getIncidentType())) {
            targetPoint = getPointForRelocation(incident, taskState);
        } else {
            targetPoint = getPointForTask(incident, taskState);
        }

        return targetPoint;
    }

    private GeoBrokerPoint getPointForRelocation(final CachedIncident incident, final TaskState taskState) {
        GeoBrokerPoint targetPoint;
        switch (taskState) {
            case Assigned:
            case ZBO:
            case ZAO:
                targetPoint = incident.getDestination();
                break;
            default:
                targetPoint = null;
                break;
        }

        return targetPoint;
    }

    private GeoBrokerPoint getPointForTask(final CachedIncident incident, final TaskState taskState) {
        GeoBrokerPoint targetPoint;
        switch (taskState) {
            case Assigned:
            case ZBO:
                targetPoint = incident.getIncident().getLocation();
                break;
            case ZAO:
                targetPoint = incident.getDestination();
                break;
            default:
                targetPoint = null;
                break;
        }

        return targetPoint;
    }
}
