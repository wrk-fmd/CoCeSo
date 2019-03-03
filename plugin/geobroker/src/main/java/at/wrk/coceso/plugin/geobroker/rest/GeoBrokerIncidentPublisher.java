package at.wrk.coceso.plugin.geobroker.rest;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;

/**
 * Publishes the provided updates to the external REST interface.
 */
public interface GeoBrokerIncidentPublisher {
    /**
     * Informs the external GeoBroker about the updated incident in gebroker format.
     */
    void incidentUpdated(GeoBrokerIncident updatedIncident);

    /**
     * Informs the external GeoBroker about the deleted incident with the given identifier.
     */
    void incidentDeleted(String externalIncidentId);

    /**
     * Re-sends the full state towards GeoBroker.
     */
    void resendFullState();
}
