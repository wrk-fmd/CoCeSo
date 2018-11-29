package at.wrk.geocode.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class IntegerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IntegerUtils.class);

    /**
     * Helper function to parse an Integer with error handling.
     *
     * @param integerString Input string to parse the integer value
     * @return The integer value of str, or null if not a valid integer
     */
    public static Optional<Integer> parseInt(final String integerString) {
        Optional<Integer> parsedValue = Optional.empty();
        if (!StringUtils.isBlank(integerString)) {
            try {
                parsedValue = Optional.of(Integer.parseInt(integerString.trim()));
            } catch (NumberFormatException e) {
                LOG.debug("Failed to parse input value '{}' to integer value.", integerString);
                LOG.trace("Underlying NumberFormatException: ", e);
            }
        }

        return parsedValue;
    }
}
