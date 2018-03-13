package at.wrk.coceso.plugin.geobroker.token;

import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Sha512TokenGenerator implements TokenGenerator {

    private final static String CHARACTER_MAPPING = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";

    private static final String STATIC_PREFIX = "CoCeSo-GeoBroker-token";

    @Override
    public String calculateToken(final String unitId, final String salt) {
        String hashInput = STATIC_PREFIX + unitId + salt;
        byte[] hash = Sha512DigestUtils.sha(hashInput);

        StringBuilder resultingToken = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            int valueToMap = Math.abs((int) hash[i]) % 64;
            char convertedChar = CHARACTER_MAPPING.charAt(valueToMap);
            resultingToken.append(convertedChar);
        }

        return resultingToken.toString();
    }
}
