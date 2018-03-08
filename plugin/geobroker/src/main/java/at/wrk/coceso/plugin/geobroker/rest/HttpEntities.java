package at.wrk.coceso.plugin.geobroker.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class HttpEntities {
    private HttpEntities() {
    }

    public static HttpEntity<String> createHttpEntityForJsonString(final String unitJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(unitJson, headers);
    }
}
