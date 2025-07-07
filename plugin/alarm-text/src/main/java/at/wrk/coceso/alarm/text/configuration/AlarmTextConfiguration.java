package at.wrk.coceso.alarm.text.configuration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Component
public class AlarmTextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextConfiguration.class);
    public static final String DEFAULT_SMS_GATEWAY_TYPE = "gammu";

    private final URI smsGatewayUri;
    private final String smsGatewayType;
    private final URI tetraGatewayUri;
    private final String validPrefix;
    private final String defaultCountryCode;
    private final String tetraAuthenticationToken;
    private final boolean requestConsumeReport;
    private final String smsAuthenticationToken;

    public AlarmTextConfiguration(
            @Value("${alarm.text.gateway.sms.uri:}") final String smsGatewayUriString,
            @Value("${alarm.text.gateway.sms.type:}") final String smsGatewayType,
            @Value("${alarm.text.gateway.tetra.uri:}") final String tetraGatewayUriString,
            @Value("${alarm.text.gateway.phone.number.prefix:}") final String validPhonePrefix,
            @Value("${alarm.text.gateway.phone.number.default.country.code:}") final String defaultCountryCode,
            @Value("${alarm.text.gateway.authenticationToken:}") final String legacyAuthenticationToken,
            @Value("${alarm.text.gateway.sms.authentication.token:}") final String smsAuthenticationToken,
            @Value("${alarm.text.gateway.tetra.authentication.token:}") final String tetraAuthenticationToken,
            @Value("${alarm.text.gateway.tetra.request.consume.report:false}") final boolean requestConsumeReport) {
        this.smsGatewayUri = parseHttpUriFromString(smsGatewayUriString);
        this.smsGatewayType = StringUtils.isNotBlank(smsGatewayType) ? smsGatewayType : DEFAULT_SMS_GATEWAY_TYPE;
        this.tetraGatewayUri = parseHttpUriFromString(tetraGatewayUriString);
        this.validPrefix = StringUtils.trimToNull(validPhonePrefix);
        this.defaultCountryCode = StringUtils.trimToNull(defaultCountryCode);
        this.tetraAuthenticationToken = tetraAuthenticationToken;
        this.requestConsumeReport = requestConsumeReport;
        this.smsAuthenticationToken = Optional.ofNullable(StringUtils.trimToNull(smsAuthenticationToken))
                .orElse(legacyAuthenticationToken);
    }

    @Nullable
    public URI getSmsGatewayUri() {
        return smsGatewayUri;
    }

    public String getSmsGatewayType() {
        return smsGatewayType;
    }

    @Nullable
    public URI getTetraGatewayUri() {
        return tetraGatewayUri;
    }

    @Nullable
    public String getValidPrefix() {
        return validPrefix;
    }

    public String getDefaultCountryCode() {
        return defaultCountryCode;
    }

    @Nullable
    public String getSmsAuthenticationToken() {
        return smsAuthenticationToken;
    }

    @Nullable
    public String getTetraAuthenticationToken() {
        return tetraAuthenticationToken;
    }

    public boolean isRequestConsumeReport() {
        return requestConsumeReport;
    }

    private static URI parseHttpUriFromString(final String uriString) {
        URI parsedUri = null;
        String trimmedUriString = StringUtils.trimToNull(uriString);
        if (trimmedUriString != null) {
            try {
                parsedUri = new URI(trimmedUriString);
            } catch (URISyntaxException e) {
                LOG.error("Invalid URI for alarm text gateway: '{}'", uriString);
            }
        }

        if (parsedUri != null
                && !StringUtils.equalsIgnoreCase(parsedUri.getScheme(), "https")
                && !StringUtils.equalsIgnoreCase(parsedUri.getScheme(), "http")) {
            LOG.warn("Configured URI is not a valid HTTP(S) URI: {}.", uriString);
            parsedUri = null;
        }

        return parsedUri;
    }
}
