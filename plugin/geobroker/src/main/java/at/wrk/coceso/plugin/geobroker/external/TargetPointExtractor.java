package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.point.Point;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class TargetPointExtractor {
    @Nullable
    public Point getTargetPoint(final Incident incident, final TaskState taskState) {
        Point targetPoint = null;

        switch (taskState) {
            case Assigned:
            case ZBO:
                targetPoint = incident.getBo();
                break;
            case ZAO:
                targetPoint = incident.getAo();
                break;
            default:
                targetPoint = null;
                break;
        }

        return targetPoint;
    }
}
