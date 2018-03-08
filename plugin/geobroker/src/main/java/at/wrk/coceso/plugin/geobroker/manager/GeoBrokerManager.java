package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;

/**
 * Collects updates of units and incidents, and triggers updates where the effected entity itself was not changed.
 * This includes:
 * <ul>
 *   <li>Incident Point updated: Triggers updates of the related units to update their target position</li>
 *   <li>Unit removed from incident: Visibility Matrix of referenced units is updated</li>
 * </ul>
 */
public interface GeoBrokerManager {

    void unitUpdated(GeoBrokerUnit unit);

    void unitDeleted(String externalUnitId);

    void incidentUpdated(GeoBrokerIncident incident);

    void incidentDeleted(String externalIncidentId);
}
