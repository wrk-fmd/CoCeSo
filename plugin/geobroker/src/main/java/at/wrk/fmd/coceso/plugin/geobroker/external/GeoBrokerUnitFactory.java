package at.wrk.fmd.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Unit;
import at.wrk.fmd.coceso.plugin.geobroker.contract.GeoBrokerUnit;

public interface GeoBrokerUnitFactory {
    GeoBrokerUnit createExternalUnit(Unit unit);
}
