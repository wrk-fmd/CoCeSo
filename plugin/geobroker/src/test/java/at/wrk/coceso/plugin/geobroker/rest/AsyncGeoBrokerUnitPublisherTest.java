package at.wrk.coceso.plugin.geobroker.rest;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerUnits;
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

public class AsyncGeoBrokerUnitPublisherTest {

    private AsyncRestTemplate restTemplate;
    private String privateApiUrl;
    private AsyncGeoBrokerUnitPublisher sut;

    @Before
    public void init() {
        restTemplate = mock(AsyncRestTemplate.class, withSettings().defaultAnswer(RETURNS_MOCKS));
        privateApiUrl = "http://local.invalid/private";
        sut = new AsyncGeoBrokerUnitPublisher(restTemplate, privateApiUrl, Converters.registerAll(new GsonBuilder()).create());
    }

    @Test
    public void unitUpdated_publishUpdate() {
        GeoBrokerUnit unit = GeoBrokerUnits.random();

        sut.unitUpdated(unit);

        verify(restTemplate).exchange(eq(privateApiUrl + "/units/" + unit.getId()), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void unitUpdatedTwoTimes_noUpdate() {
        GeoBrokerUnit unit = GeoBrokerUnits.random();
        sut.unitUpdated(unit);
        reset(restTemplate);

        sut.unitUpdated(unit);

        verifyZeroInteractions(restTemplate);
    }

    @Test
    public void unitDeleted_updatedBefore_publishDeletion() {
        GeoBrokerUnit unit = GeoBrokerUnits.random();
        sut.unitUpdated(unit);
        reset(restTemplate);

        sut.unitDeleted(unit.getId());

        verify(restTemplate).delete(eq(privateApiUrl + "/units/" + unit.getId()));
    }

    @Test
    public void unitDeleted_unknownUnit_noDeletionPublished() {
        reset(restTemplate);

        sut.unitDeleted(randomString());

        verifyZeroInteractions(restTemplate);
    }
}