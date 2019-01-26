package at.wrk.coceso.alarm.text.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class AlarmTextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextConfiguration.class);

    private final URI smsGatewayUrl;
    private final URI tetraGatewayUrlString;
    private final String validPrefix;
    private final String defaultCountryCode;
    private final String authenticationToken;

    public AlarmTextConfiguration(
            @Value("${alarm.text.gateway.sms.uri}") final String smsGatewayUrl,
            @Value("${alarm.text.gateway.tetra.uri}") final String tetraGatewayUrlString,
            @Value("${alarm.text.gateway.phone.number.prefix}") final String validPhonePrefix,
            @Value("${alarm.text.gateway.phone.number.default.country.code}") final String defaultCountryCode,
            @Value("${alarm.text.gateway.authenticationToken}") final String authenticationToken) {
        this.smsGatewayUrl = parseUriFromString(smsGatewayUrl);
        this.tetraGatewayUrlString = parseUriFromString(tetraGatewayUrlString);
        this.validPrefix = trimIfNotNull(validPhonePrefix);
        this.defaultCountryCode = trimIfNotNull(defaultCountryCode);
        this.authenticationToken = trimIfNotNull(authenticationToken);
    }

    @Nullable
    public URI getSmsGatewayUrl() {
        return smsGatewayUrl;
    }

    @Nullable
    public URI getTetraGatewayUrlString() {
        return tetraGatewayUrlString;
    }

    @Nullable
    public String getValidPrefix() {
        return validPrefix;
    }

    public String getDefaultCountryCode() {
        return defaultCountryCode;
    }

    @Nullable
    public String getAuthenticationToken() {
        return authenticationToken;
    }

    private static URI parseUriFromString(final String urlString) {
        URI parsedUri = null;
        if (urlString != null) {
            try {
                parsedUri = new URI(urlString.trim());
            } catch (URISyntaxException e) {
                LOG.error("Invalid URL for alarm text gateway: '{}'", urlString);
            }
        }

        return parsedUri;
    }

    private static String trimIfNotNull(final String inputString) {
        return inputString == null ? null : inputString.trim();
    }
}
