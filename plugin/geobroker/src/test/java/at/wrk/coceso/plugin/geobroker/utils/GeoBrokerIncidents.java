package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;

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
                randomPoint());
    }
}
