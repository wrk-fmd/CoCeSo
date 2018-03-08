package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
                ImmutableMap.of(randomString(), TaskState.ABO),
                randomPoint(),
                randomPoint()
        );
    }
}
