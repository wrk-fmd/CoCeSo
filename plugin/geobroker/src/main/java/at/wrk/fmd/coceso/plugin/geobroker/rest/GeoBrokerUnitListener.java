package at.wrk.fmd.coceso.plugin.geobroker.rest;

import at.wrk.fmd.coceso.plugin.geobroker.contract.GeoBrokerUnit;

public interface GeoBrokerUnitListener {
    void unitUpdated(GeoBrokerUnit updatedUnit);

    void unitDeleted(String externalUnitId);
}
