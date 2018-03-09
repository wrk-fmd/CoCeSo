package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import com.google.common.collect.ImmutableList;

import static at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints.randomPoint;
import static at.wrk.coceso.plugin.geobroker.utils.Strings.randomString;

public final class GeoBrokerUnits {
    private GeoBrokerUnits() {
    }

    public static GeoBrokerUnit random() {
        return random(randomString());
    }

    public static GeoBrokerUnit random(final String externalUnitId) {
        return new GeoBrokerUnit(
                externalUnitId,
                randomString(),
                randomString(),
                ImmutableList.of(randomString()),
                ImmutableList.of(randomString()),
                randomPoint(),
                randomPoint()
        );
    }
}
