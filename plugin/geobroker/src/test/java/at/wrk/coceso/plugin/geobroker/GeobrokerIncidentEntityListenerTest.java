package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.external.ExternalIncidentIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerIncidentFactory;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class GeobrokerIncidentEntityListenerTest {
    private GeobrokerIncidentEntityListener sut;
    private GeoBrokerManager geoBrokerManager;
    private ExternalIncidentIdGenerator incidentIdGenerator;
    private GeoBrokerIncidentFactory incidentFactory;

    @Before
    public void init() {
        geoBrokerManager = mock(GeoBrokerManager.class);
        incidentIdGenerator = mock(ExternalIncidentIdGenerator.class);
        incidentFactory = mock(GeoBrokerIncidentFactory.class);
        sut = new GeobrokerIncidentEntityListener(geoBrokerManager, incidentIdGenerator, incidentFactory);
    }

    @Parameters({"Open", "Demand", "InProgress"})
    @Test
    public void incidentUpdated_supportedIncident_callUpdate(final IncidentState incidentState) {
        Incident incident = new Incident(42);
        incident.setState(incidentState);
        CachedIncident cachedIncident = mock(CachedIncident.class);
        when(incidentFactory.createExternalIncident(incident)).thenReturn(cachedIncident);

        sut.entityChanged(incident, 5, 0, 0);

        verify(geoBrokerManager).incidentUpdated(cachedIncident);
    }

    @Test
    public void incidentUpdated_incidentIsDone_callDelete() {
        Incident incident = new Incident(42);
        incident.setState(IncidentState.Done);

        String externalIncidentId = "extId-5";
        when(incidentIdGenerator.generateExternalIncidentId(incident.getId(), 5)).thenReturn(externalIncidentId);

        sut.entityChanged(incident, 5, 0, 0);

        verify(geoBrokerManager).incidentDeleted(externalIncidentId);
    }
}