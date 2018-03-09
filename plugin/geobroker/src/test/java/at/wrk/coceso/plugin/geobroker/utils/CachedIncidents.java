package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints.randomPoint;
import static at.wrk.coceso.plugin.geobroker.utils.Strings.randomString;

public final class CachedIncidents {
    private CachedIncidents() {
    }

    public static CachedIncident random() {
        return random(ImmutableList.of(randomString()));
    }

    public static CachedIncident random(final List<String> assignedExternalUnitIds) {
        GeoBrokerIncident geoBrokerIncident = GeoBrokerIncidents.random();
        return new CachedIncident(
                geoBrokerIncident,
                assignedExternalUnitIds,
                randomPoint(),
                1);
    }
}
