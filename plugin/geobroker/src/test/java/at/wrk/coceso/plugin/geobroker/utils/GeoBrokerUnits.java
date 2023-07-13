package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.contract.broker.OneTimeAction;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

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
                List.of(randomString()),
                List.of(randomString()),
                randomPoint(),
                randomPoint(),
                RandomUtils.nextBoolean(),
                List.of(
                        new OneTimeAction("testingAction", "https://invalid.server.local/foobar", null, null)
                ));
    }
}
