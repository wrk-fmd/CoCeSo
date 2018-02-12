package at.wrk.fmd.coceso.plugin.geobroker.rest;

import at.wrk.fmd.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class AsyncGeoBrokerUnitListener implements GeoBrokerUnitListener {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncGeoBrokerUnitListener.class);

    private final AsyncRestTemplate restTemplate;
    private final String privateApiUrl;
    private final Gson gson;

    @Autowired
    public AsyncGeoBrokerUnitListener(
            final AsyncRestTemplate restTemplate,
            final @Value("${geobroker.private.api.url}") String privateApiUrl) {
        this.restTemplate = restTemplate;
        this.privateApiUrl = privateApiUrl;
        this.gson = Converters.registerAll(new GsonBuilder()).create();
    }

    @Override
    public void unitUpdated(final GeoBrokerUnit updatedUnit) {
        String url = getUrlForUnit(updatedUnit.getId());

        HttpEntity<String> httpEntity = serializeUnit(updatedUnit);
        putHttpEntityToUrl(url, httpEntity);
    }

    @Override
    public void unitDeleted(final String externalUnitId) {
        String url = getUrlForUnit(externalUnitId);
        ListenableFuture<?> future = restTemplate.delete(url);
        future.addCallback(
                success -> LOG.debug("Successfully deleted unit at geobroker url: '{}'", url),
                failure -> LOG.warn("Failed to delete unit at geobroker url: '{}'.", url));
    }

    private HttpEntity<String> serializeUnit(final GeoBrokerUnit updatedUnit) {
        String unitJson = gson.toJson(updatedUnit);
        return createHttpEntityForJsonString(unitJson);
    }

    private String getUrlForUnit(final String externalUnitId) {
        return privateApiUrl + "/units/" + externalUnitId;
    }

    private void putHttpEntityToUrl(final String url, final HttpEntity<String> httpEntity) {
        ListenableFuture<ResponseEntity<String>> responseFuture = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        responseFuture.addCallback(
                success -> LOG.debug("Successfully written updated unit to geobroker url: '{}'", url),
                failure -> LOG.warn("Failed to update unit at geobroker url: '{}'.", url));
    }

    private HttpEntity<String> createHttpEntityForJsonString(final String unitJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(unitJson, headers);
    }
}
