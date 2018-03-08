package at.wrk.coceso.plugin.geobroker.utils;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import org.apache.commons.lang3.RandomUtils;

public class GeoBrokerPoints {
    public static GeoBrokerPoint randomPoint() {
        return new GeoBrokerPoint(RandomUtils.nextDouble(), RandomUtils.nextDouble());
    }
}
