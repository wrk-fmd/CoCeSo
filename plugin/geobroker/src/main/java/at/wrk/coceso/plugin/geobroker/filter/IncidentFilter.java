package at.wrk.coceso.plugin.geobroker.filter;

import at.wrk.coceso.plugin.geobroker.data.CachedIncident;

public interface IncidentFilter {
    /**
     * Returns true if the incident is relevant for publishing it.
     */
    boolean isIncidentRelevantForGeoBroker(CachedIncident incident);
}
