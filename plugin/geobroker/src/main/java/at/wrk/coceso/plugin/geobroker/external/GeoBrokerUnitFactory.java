package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;

public interface GeoBrokerUnitFactory {
    CachedUnit createExternalUnit(Unit unit);
}
