package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.external.ExternalIncidentIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerIncidentFactory;
import at.wrk.coceso.plugin.geobroker.loader.IncidentLoader;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeobrokerIncidentEntityListenerTest {
    private GeobrokerIncidentEntityListener sut;
    private GeoBrokerManager geoBrokerManager;
    private GeoBrokerIncidentFactory incidentFactory;
    private IncidentLoader incidentLoader;

    @Before
    public void init() {
        geoBrokerManager = mock(GeoBrokerManager.class);
        ExternalIncidentIdGenerator incidentIdGenerator = mock(ExternalIncidentIdGenerator.class);
        incidentFactory = mock(GeoBrokerIncidentFactory.class);
        incidentLoader = mock(IncidentLoader.class);
        sut = new GeobrokerIncidentEntityListener(geoBrokerManager, incidentIdGenerator, incidentFactory, incidentLoader);
    }

    @Test
    public void incidentUpdated_supportedIncident_callUpdate() {
        Incident incident = new Incident(42);
        CachedIncident cachedIncident = mock(CachedIncident.class);
        when(incidentFactory.createExternalIncident(incident)).thenReturn(cachedIncident);

        sut.entityChanged(incident, 5, 0, 0);

        verify(geoBrokerManager).incidentUpdated(cachedIncident);
    }

    @Test
    public void contextRefreshed_supportedIncident_callUpdate() {
        Incident incident = new Incident(42);
        CachedIncident cachedIncident = mock(CachedIncident.class);
        when(incidentFactory.createExternalIncident(incident)).thenReturn(cachedIncident);

        when(incidentLoader.loadAllIncidentsOfActiveConcerns()).thenReturn(ImmutableSet.of(incident));

        sut.onContextRefreshed(null);

        verify(geoBrokerManager).incidentUpdated(cachedIncident);
    }
}