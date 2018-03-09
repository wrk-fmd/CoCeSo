package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;

/**
 * Collects updates of units and incidents, and triggers updates where the effected entity itself was not changed.
 * This includes:
 * <ul>
 *   <li>Incident Point updated: Triggers updates of the related units to update their target position</li>
 *   <li>Unit removed from incident: Visibility Matrix of referenced units is updated</li>
 * </ul>
 */
public interface GeoBrokerManager {

    void unitUpdated(CachedUnit unit);

    void unitDeleted(String externalUnitId);

    void incidentUpdated(CachedIncident incident);

    void incidentDeleted(String externalIncidentId);
}
