package at.wrk.coceso.alarm.text.service.normalizer;

import com.google.common.base.CharMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.NotThreadSafe;

@Component
@NotThreadSafe
public class TetraIssiNormalizer implements NumberNormalizer {
    private static final Logger LOG = LoggerFactory.getLogger(TetraIssiNormalizer.class);

    private static final String ALLOWED_CHARACTERS = " 0123456789";

    private final CharMatcher charMatcher;

    @Autowired
    public TetraIssiNormalizer() {
        charMatcher = CharMatcher.anyOf(ALLOWED_CHARACTERS);
    }

    @Override
    public String getSupportedUriSchema() {
        return "tetra";
    }

    @Override
    public String normalize(final String inputNumber) {
        String normalizedString;

        if (inputNumber != null && charMatcher.matchesAllOf(inputNumber)) {
            normalizedString = inputNumber.replaceAll(" ", "");
        } else {
            LOG.debug("Invalid ISSI: '{}'", inputNumber);
            normalizedString = "";
        }

        return normalizedString;
    }
}
