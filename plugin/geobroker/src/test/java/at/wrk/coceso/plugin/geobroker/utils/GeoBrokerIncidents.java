package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints.randomPoint;
import static at.wrk.coceso.plugin.geobroker.utils.Strings.randomString;

public final class GeoBrokerIncidents {
    private GeoBrokerIncidents() {
    }

    public static GeoBrokerIncident random() {
        return random(ImmutableList.of(randomString()));
    }

    public static GeoBrokerIncident random(final List<String> assignedExternalUnitIds) {
        return new GeoBrokerIncident(
                randomString(),
                randomString(),
                true,
                true,
                randomString(),
                randomPoint(),
                assignedExternalUnitIds,
                randomPoint());
    }
}
