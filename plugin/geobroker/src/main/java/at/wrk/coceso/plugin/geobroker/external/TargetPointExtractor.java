package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class TargetPointExtractor {
    @Nullable
    public GeoBrokerPoint getTargetPoint(final CachedIncident incident, final TaskState taskState) {
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
