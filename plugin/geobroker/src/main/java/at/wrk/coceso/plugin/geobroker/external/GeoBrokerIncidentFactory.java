package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;

public interface GeoBrokerIncidentFactory {
    GeoBrokerIncident createExternalIncident(Incident incident);
}
