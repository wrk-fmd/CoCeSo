package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.plugin.geobroker.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class ExternalUnitIdGenerator {

    /**
     * The salt is configured system wide. To generate different values for external unit id and token, the salt is prefixed differently.
     */
    private static final String SALT_PREFIX = "EXT-UNIT-ID:";

    private final TokenGenerator generator;
    private final String salt;

    @Autowired
    public ExternalUnitIdGenerator(
            final TokenGenerator generator,
            @Value("${geobroker.salt:default}") final String salt) {
        this.generator = generator;
        this.salt = salt;
    }

    public String generateExternalUnitId(final int unitId, final int concernId) {
        String unitIdString = Integer.toString(unitId);
        String concernPrefix = Integer.toString(concernId) + "-";

        return concernPrefix + generator.calculateToken(unitIdString, SALT_PREFIX + salt);
    }
}
