package at.wrk.coceso.plugins.vienna.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PostalCodeUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PostalCodeUtil.class);

    public Optional<String> createPostalCodeForDistrictNumber(final String districtNumber) {
        Optional<String> postalCode = Optional.empty();
        try {
            int parsedDistrictNumber = Integer.parseInt(districtNumber);
            if (parsedDistrictNumber > 0 && parsedDistrictNumber <= 23) {
                postalCode = Optional.of(String.format("1%02d0", parsedDistrictNumber));
            } else {
                LOG.warn("District number is out of range: {}", parsedDistrictNumber);
            }
        } catch (NumberFormatException e) {
            LOG.warn("Error processing district number '{}'. Cannot create postal code.", districtNumber);
        }

        return postalCode;
    }
}
