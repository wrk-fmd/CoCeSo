package at.wrk.coceso.utils;

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
