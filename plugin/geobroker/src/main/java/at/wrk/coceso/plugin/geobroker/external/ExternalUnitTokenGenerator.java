package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.plugin.geobroker.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class ExternalUnitTokenGenerator {

    /**
     * The salt is configured system wide. To generate different values for external unit id and token, the salt is prefixed differently.
     */
    private static final String SALT_PREFIX = "TOKEN:";

    private final TokenGenerator generator;
    private final String salt;

    @Autowired
    public ExternalUnitTokenGenerator(
            final TokenGenerator generator,
            @Value("${geobroker.salt}") final String salt) {
        this.generator = generator;
        this.salt = salt;
    }

    public String generateToken(final Unit unit) {
        String unitIdString = Integer.toString(unit.getId());
        return generator.calculateToken(unitIdString, SALT_PREFIX + salt);
    }
}
