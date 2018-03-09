package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;

public interface GeoBrokerIncidentFactory {
    CachedIncident createExternalIncident(Incident incident);
}
