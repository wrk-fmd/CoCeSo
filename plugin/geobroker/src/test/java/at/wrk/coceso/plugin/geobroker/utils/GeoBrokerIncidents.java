package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerIncident;
import com.google.common.collect.ImmutableMap;

import static at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints.randomPoint;
import static at.wrk.coceso.plugin.geobroker.utils.Strings.randomString;

public final class GeoBrokerIncidents {
    private GeoBrokerIncidents() {
    }

    public static GeoBrokerIncident random() {
        return new GeoBrokerIncident(
                randomString(),
                randomString(),
                true,
                true,
                randomString(),
                randomPoint(),
                randomPoint(),
                ImmutableMap.of("externalUnitId-42", "ZBO"));
    }
}
