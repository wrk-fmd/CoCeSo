package at.wrk.coceso.plugin.geobroker.token;

import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Sha512TokenGenerator implements TokenGenerator {

    private static final String STATIC_PREFIX = "CoCeSo-GeoBroker-token";

    @Override
    public String calculateToken(final String unitId, final String salt) {
        String hashInput = STATIC_PREFIX + unitId + salt;
        return Sha512DigestUtils.shaHex(hashInput);
    }
}
