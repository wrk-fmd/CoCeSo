package at.wrk.coceso.alarm.text.configuration;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AlarmTextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextConfiguration.class);

    private final URI alarmTextGatewayUrl;
    private final String validPrefix;
    private final Set<String> transparentUriSchemas;
    private final String authenticationToken;

    public AlarmTextConfiguration(
            @Value("${alarm.text.gateway.uri}") final String alarmTextGatewayUrlString,
            @Value("${alarm.text.gateway.phone.number.prefix}") final String validPhonePrefix,
            @Value("${alarm.text.gateway.transparent.uri.schemas}") final String transparentUriSchemas,
            @Value("${alarm.text.gateway.authenticationToken}") final String authenticationToken) {
        this.alarmTextGatewayUrl = parseUriFromString(alarmTextGatewayUrlString);
        this.validPrefix = trimIfNotNull(validPhonePrefix);
        this.transparentUriSchemas = createListFromCommaSeparatedString(transparentUriSchemas);
        this.authenticationToken = trimIfNotNull(authenticationToken);
    }

    private Set<String> createListFromCommaSeparatedString(final String transparentUriSchemas) {
        return transparentUriSchemas == null ?
                ImmutableSet.of() :
                ImmutableSet.copyOf(
                        Stream.of(transparentUriSchemas.split(","))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toSet()));
    }

    @Nullable
    public URI getAlarmTextGatewayUrl() {
        return alarmTextGatewayUrl;
    }

    @Nullable
    public String getValidPrefix() {
        return validPrefix;
    }

    public Set<String> getTransparentUriSchemas() {
        return transparentUriSchemas;
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
