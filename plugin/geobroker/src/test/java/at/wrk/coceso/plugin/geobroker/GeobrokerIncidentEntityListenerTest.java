package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.external.ExternalIncidentIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerIncidentFactory;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeobrokerIncidentEntityListenerTest {
    private GeobrokerIncidentEntityListener sut;
    private GeoBrokerManager geoBrokerManager;
    private GeoBrokerIncidentFactory incidentFactory;

    @Before
    public void init() {
        geoBrokerManager = mock(GeoBrokerManager.class);
        ExternalIncidentIdGenerator incidentIdGenerator = mock(ExternalIncidentIdGenerator.class);
        incidentFactory = mock(GeoBrokerIncidentFactory.class);
        sut = new GeobrokerIncidentEntityListener(geoBrokerManager, incidentIdGenerator, incidentFactory);
    }

    @Test
    public void incidentUpdated_supportedIncident_callUpdate() {
        Incident incident = new Incident(42);
        CachedIncident cachedIncident = mock(CachedIncident.class);
        when(incidentFactory.createExternalIncident(incident)).thenReturn(cachedIncident);

        sut.entityChanged(incident, 5, 0, 0);

        verify(geoBrokerManager).incidentUpdated(cachedIncident);
    }
}