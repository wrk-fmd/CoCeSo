package at.wrk.coceso.plugin.geobroker.rest;

import at.wrk.coceso.plugin.geobroker.contract.StatusResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Component
public class GeoBrokerHeartbeatScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(GeoBrokerHeartbeatScheduler.class);
    private static final int DELAY_BETWEEN_HEARTBEATS = 15000;

    private final RestTemplate restTemplate;
    private final String privateApiUrl;
    private final Gson gson;
    private final GeoBrokerIncidentPublisher incidentPublisher;
    private final GeoBrokerUnitPublisher unitPublisher;

    private String lastReceivedInstanceId;

    @Autowired
    public GeoBrokerHeartbeatScheduler(
            final RestTemplate restTemplate,
            final @Value("${geobroker.private.api.url}") String privateApiUrl,
            final Gson gson,
            final GeoBrokerIncidentPublisher incidentPublisher,
            final GeoBrokerUnitPublisher unitPublisher) {
        this.restTemplate = restTemplate;
        this.privateApiUrl = privateApiUrl;
        this.gson = gson;
        this.incidentPublisher = incidentPublisher;
        this.unitPublisher = unitPublisher;
        this.lastReceivedInstanceId = null;
    }

    @Scheduled(fixedDelay = DELAY_BETWEEN_HEARTBEATS)
    public void checkGeoBrokerInstance() {
        LOG.debug("Checking instance ID of geobroker.");

        Optional<StatusResponse> statusResponse = getStatusResponse();

        statusResponse.ifPresent(this::checkIfGeoBrokerInstanceChanged);
    }

    private void checkIfGeoBrokerInstanceChanged(final StatusResponse statusResponse) {
        String receivedInstanceId = statusResponse.getInstanceId();
        if (receivedInstanceId != null) {
            if (!Objects.equals(lastReceivedInstanceId, receivedInstanceId)) {
                LOG.info(
                        "Instance of geobroker changed from '{}' to '{}'. Full update of units and incidents is sent.",
                        lastReceivedInstanceId,
                        receivedInstanceId);

                unitPublisher.resendFullState();
                incidentPublisher.resendFullState();
            } else {
                LOG.trace("No update towards geobroker needed.");
            }

            this.lastReceivedInstanceId = receivedInstanceId;
        } else {
            LOG.debug("Did not receive a valid instance ID from geobroker. No further action performed.");
        }
    }

    private Optional<StatusResponse> getStatusResponse() {
        Optional<StatusResponse> statusResponse = Optional.empty();
        String url = privateApiUrl + "/status";

        ResponseEntity<String> responseEntity = getResponseEntity(url);

        if (responseEntity != null) {
            statusResponse = handleStatusResponse(responseEntity);
        }

        return statusResponse;
    }

    private Optional<StatusResponse> handleStatusResponse(final ResponseEntity<String> responseEntity) {
        Optional<StatusResponse> statusResponse = Optional.empty();
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            LOG.warn("Geobroker did not return return 200 OK on status query. Actual code: {}", responseEntity.getStatusCode());
        } else {
            try {
                statusResponse = Optional.ofNullable(gson.fromJson(responseEntity.getBody(), StatusResponse.class));
            } catch (JsonSyntaxException e) {
                LOG.warn("Status response of geobroker is not a JSON response", e);
            }
        }

        return statusResponse;
    }

    private ResponseEntity<String> getResponseEntity(final String url) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(url, String.class);
        } catch (RestClientException e) {
            LOG.warn("Geobroker returned an error on checking status: " + e.getMessage());
            LOG.trace("Underlying exception", e);
        }

        return responseEntity;
    }
}
