package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;

import java.util.Map;

import static at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints.randomPoint;
import static at.wrk.coceso.plugin.geobroker.utils.Strings.randomString;

public final class CachedIncidents {
    private CachedIncidents() {
    }

    public static CachedIncident random() {
        return random(Map.of(randomString(), TaskState.ZBO));
    }

    public static CachedIncident random(final Map<String, TaskState> assignedExternalUnitIds) {
        GeoBrokerIncident geoBrokerIncident = GeoBrokerIncidents.random();
        return new CachedIncident(
                geoBrokerIncident,
                assignedExternalUnitIds,
                randomPoint(),
                1,
                5,
                IncidentType.Task,
                IncidentState.InProgress);
    }
}
