package at.wrk.coceso.plugin.geobroker.rest;

import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerIncidents;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.AsyncRestTemplate;

import static at.wrk.coceso.plugin.geobroker.utils.Strings.randomString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.withSettings;

public class AsyncGeoBrokerIncidentPublisherTest {

    private AsyncRestTemplate restTemplate;
    private String privateApiUrl;
    private AsyncGeoBrokerIncidentPublisher sut;

    @Before
    public void init() {
        restTemplate = mock(AsyncRestTemplate.class, withSettings().defaultAnswer(RETURNS_MOCKS));
        privateApiUrl = "http://local.invalid/private";
        sut = new AsyncGeoBrokerIncidentPublisher(restTemplate, privateApiUrl, Converters.registerAll(new GsonBuilder()).create());
    }

    @Test
    public void incidentUdated_publishUpdate() {
        GeoBrokerIncident incident = GeoBrokerIncidents.random();

        sut.incidentUpdated(incident);

        verify(restTemplate).exchange(eq(privateApiUrl + "/incidents/" + incident.getId()), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void incidentUpdatedTwoTimes_noUpdate() {
        GeoBrokerIncident incident = GeoBrokerIncidents.random();
        sut.incidentUpdated(incident);
        reset(restTemplate);

        sut.incidentUpdated(incident);

        verifyZeroInteractions(restTemplate);
    }

    @Test
    public void incidentDeleted_updatedBefore_publishDeletion() {
        GeoBrokerIncident incident = GeoBrokerIncidents.random();
        sut.incidentUpdated(incident);
        reset(restTemplate);

        sut.incidentDeleted(incident.getId());

        verify(restTemplate).delete(eq(privateApiUrl + "/incidents/" + incident.getId()));
    }

    @Test
    public void incidentDeleted_unknownIncident_noDeletionPublished() {
        reset(restTemplate);

        sut.incidentDeleted(randomString());

        verifyZeroInteractions(restTemplate);
    }
}
