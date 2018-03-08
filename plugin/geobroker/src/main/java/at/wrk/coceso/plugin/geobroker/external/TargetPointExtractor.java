package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class TargetPointExtractor {
    @Nullable
    public GeoBrokerPoint getTargetPoint(final GeoBrokerIncident incident, final TaskState taskState) {
        GeoBrokerPoint targetPoint = null;

        switch (taskState) {
            case Assigned:
            case ZBO:
                targetPoint = incident.getLocation();
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
