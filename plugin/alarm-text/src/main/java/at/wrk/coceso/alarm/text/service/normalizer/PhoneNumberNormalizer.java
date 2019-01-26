package at.wrk.coceso.alarm.text.service.normalizer;

import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.NotThreadSafe;

@Component
@NotThreadSafe
public class PhoneNumberNormalizer implements NumberNormalizer {
    private static final Logger LOG = LoggerFactory.getLogger(PhoneNumberNormalizer.class);

    private static final String ALLOWED_CHARACTERS = "+0123456789";

    private final CharMatcher charMatcher;
    private final String validPrefix;
    private final String defaultCountryCode;

    @Autowired
    public PhoneNumberNormalizer(final AlarmTextConfiguration alarmTextConfiguration) {
        String configuredPrefix = alarmTextConfiguration.getValidPrefix();
        this.validPrefix = configuredPrefix == null ? getDefaultPrefix() : configuredPrefix;

        String configuredCountryCode = alarmTextConfiguration.getDefaultCountryCode();
        this.defaultCountryCode = configuredCountryCode == null ? getDefaultPrefix() : configuredCountryCode;
        charMatcher = CharMatcher.anyOf(ALLOWED_CHARACTERS);
    }

    @Override
    public String getSupportedUriSchema() {
        return "tel";
    }

    @Override
    public String normalize(final String inputNumber) {
        String normalizedString = "";
        if (StringUtils.isNotBlank(inputNumber)) {
            normalizedString = charMatcher.retainFrom(inputNumber);
            if (normalizedString.matches("^0[1-9][0-9]+")) {
                LOG.trace("Using default country code for number '{}'.", inputNumber);
                normalizedString = normalizedString.replaceFirst("0", defaultCountryCode);
            }

            if (normalizedString.matches("^00[1-9][0-9]+")) {
                normalizedString = normalizedString.replaceFirst("00", "+");
            }

            if (!normalizedString.startsWith(validPrefix)) {
                LOG.debug("Number is dropped during normalization for alarm text: input='{}', normalized='{}', validPrefix='{}'", inputNumber, normalizedString, validPrefix);
                normalizedString = "";
            }
        } else {
            LOG.trace("Tried to normalize invalid number: '{}'", inputNumber);
        }

        return normalizedString;
    }

    private static String getDefaultPrefix() {
        return "+43";
    }
}
