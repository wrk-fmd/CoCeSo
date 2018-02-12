package at.wrk.coceso.plugin.geobroker.rest;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;

public interface GeoBrokerUnitListener {
    void unitUpdated(GeoBrokerUnit updatedUnit);

    void unitDeleted(String externalUnitId);
}
