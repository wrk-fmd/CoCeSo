package at.wrk.coceso.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class HttpEntities {
    private HttpEntities() {
    }

    public static HttpEntity<String> createHttpEntityForJsonString(final String unitJson) {
        HttpHeaders headers = createJsonApplicationType();

        return new HttpEntity<>(unitJson, headers);
    }

    public static HttpEntity<String> createHttpEntityForJsonString(final String unitJson, final String basicAuthConcatenatedString) {
        HttpHeaders headers = createJsonApplicationType();

        String encodedString = encodeBasicAuthCredentials(basicAuthConcatenatedString);
        headers.add("Authorization", "Basic " + encodedString);

        return new HttpEntity<>(unitJson, headers);
    }

    private static String encodeBasicAuthCredentials(final String stringToEncode) {
        return Base64.getEncoder().encodeToString(stringToEncode.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpHeaders createJsonApplicationType() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
