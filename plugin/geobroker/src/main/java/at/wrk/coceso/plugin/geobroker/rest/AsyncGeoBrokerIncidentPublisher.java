package at.wrk.coceso.plugin.geobroker.rest;

import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static at.wrk.coceso.utils.HttpEntities.createHttpEntityForJsonString;

@Component
public class AsyncGeoBrokerIncidentPublisher implements GeoBrokerIncidentPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncGeoBrokerIncidentPublisher.class);

    private final AsyncRestTemplate restTemplate;
    private final String privateApiUrl;
    private final Gson gson;

    private final Map<String, GeoBrokerIncident> incidentCache;

    @Autowired
    public AsyncGeoBrokerIncidentPublisher(
            final AsyncRestTemplate restTemplate,
            final @Value("${geobroker.private.api.url}") String privateApiUrl,
            final Gson gson) {
        this.restTemplate = restTemplate;
        this.privateApiUrl = privateApiUrl;
        this.gson = gson;

        this.incidentCache = new ConcurrentHashMap<>();
    }

    @Override
    public void incidentUpdated(final GeoBrokerIncident updatedIncident) {
        GeoBrokerIncident previousValue = incidentCache.put(updatedIncident.getId(), updatedIncident);
        if (!Objects.equals(previousValue, updatedIncident)) {
            publishIncidentUpdate(updatedIncident);
        } else {
            LOG.debug("Incident was not changed since last update. Publishing is skipped. incidentId={}", updatedIncident.getId());
        }
    }

    @Override
    public void incidentDeleted(final String externalIncidentId) {
        GeoBrokerIncident removedIncident = incidentCache.remove(externalIncidentId);
        if (removedIncident != null) {
            publishIncidentDeletion(externalIncidentId);
        } else {
            LOG.debug("Incident was not published to GeoBroker before. Deletion is skipped. incidentId={}", externalIncidentId);
        }
    }

    @Override
    public void resendFullState() {
        incidentCache.values().forEach(this::publishIncidentUpdate);
    }

    private void publishIncidentDeletion(final String externalIncidentId) {
        String url = getUrlForIncident(externalIncidentId);
        ListenableFuture<?> future = restTemplate.delete(url);
        future.addCallback(
                success -> LOG.debug("Successfully deleted incident at geobroker url: '{}'", url),
                failure -> LOG.warn("Failed to delete incident at geobroker url: '{}'.", url));
    }

    private void publishIncidentUpdate(final GeoBrokerIncident updatedIncident) {
        String url = getUrlForIncident(updatedIncident.getId());
        HttpEntity<String> jsonString = serializeIncident(updatedIncident);
        putHttpEntityToUrl(url, jsonString);
    }

    private HttpEntity<String> serializeIncident(final GeoBrokerIncident updatedIncident) {
        String incidentJson = gson.toJson(updatedIncident);
        return createHttpEntityForJsonString(incidentJson);
    }

    private String getUrlForIncident(final String externalIncidentId) {
        return privateApiUrl + "/incidents/" + externalIncidentId;
    }

    private void putHttpEntityToUrl(final String url, final HttpEntity<String> httpEntity) {
        ListenableFuture<ResponseEntity<String>> responseFuture = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        responseFuture.addCallback(
                success -> LOG.debug("Successfully written updated incident to geobroker url: '{}'", url),
                failure -> LOG.warn("Failed to update incident at geobroker url: '{}'.", url));
    }
}
