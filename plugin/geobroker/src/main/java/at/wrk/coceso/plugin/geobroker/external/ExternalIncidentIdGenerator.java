package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class ExternalIncidentIdGenerator {

    /**
     * The salt is configured system wide. To generate different values for external unit id and token, the salt is prefixed differently.
     */
    private static final String SALT_PREFIX = "EXT-INCIDENT-ID:";

    private final TokenGenerator generator;
    private final String salt;

    @Autowired
    public ExternalIncidentIdGenerator(
            final TokenGenerator generator,
            @Value("${geobroker.salt}") final String salt) {
        this.generator = generator;
        this.salt = salt;
    }

    public String generateExternalIncidentId(final Incident incident) {
        String unitIdString = Integer.toString(incident.getId());
        String concernPrefix = getConcernPrefixForUnit(incident);

        return concernPrefix + generator.calculateToken(unitIdString, SALT_PREFIX + salt);
    }

    private String getConcernPrefixForUnit(final Incident incident) {
        return Optional.ofNullable(incident.getConcern())
                    .map(Concern::getId)
                    .map(Object::toString)
                    .map(idString -> idString + "-")
                    .orElse("");
    }
}
