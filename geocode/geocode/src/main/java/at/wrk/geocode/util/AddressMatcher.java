package at.wrk.geocode.util;

import at.wrk.geocode.address.Address;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddressMatcher {

    private static final double MINIMUM_LEVENSHTEIN_TO_LENGTH_RATION_TO_MATCH = 0.2;

    private final LevenshteinDistance levenshteinDistance;
    private final AddressNumberMatcher addressNumberMatcher;

    @Autowired
    public AddressMatcher(
            final LevenshteinDistance levenshteinDistance,
            final AddressNumberMatcher addressNumberMatcher) {
        this.levenshteinDistance = levenshteinDistance;
        this.addressNumberMatcher = addressNumberMatcher;
    }

    /**
     * Non-symmetrical check if found address is a match
     *
     * @param foundAddress             The found address by the search request (search hit)
     * @param searchInputAddress       The address searched for (search input)
     * @param exactNumberMatchRequired Search for an exact match of numbers
     * @return True iff post code of b matches (if given), street is a close match (as given by {@link #isStreetMatchingByLevenshtein}) and number
     * of a matches the one of b exactly/is contained within range given in b
     */
    public boolean isFoundAddressMatching(final Address foundAddress, Address searchInputAddress, final boolean exactNumberMatchRequired) {
        if (foundAddress == null || searchInputAddress == null) {
            return false;
        }

        return isNumberOfAddressMatching(foundAddress, searchInputAddress, exactNumberMatchRequired)
                && isPostCodeMatching(foundAddress, searchInputAddress)
                && isStreetMatchingByLevenshtein(foundAddress, searchInputAddress);
    }

    private boolean isPostCodeMatching(final Address foundAddress, final Address searchInputAddress) {
        return searchInputAddress.getPostCode() == null || Objects.equals(foundAddress.getPostCode(), searchInputAddress.getPostCode());
    }

    private boolean isNumberOfAddressMatching(
            final Address foundAddress,
            final Address searchInputAddress,
            final boolean exactNumberMatchRequired) {
        return exactNumberMatchRequired
                ? addressNumberMatcher.exactMatch(foundAddress.getNumber(), searchInputAddress.getNumber())
                : addressNumberMatcher.contains(foundAddress.getNumber(), searchInputAddress.getNumber());
    }

    /**
     * Check if both streetnames match
     *
     * @param leftAddress  Left address to compare.
     * @param rightAddress Right address to compare.
     * @return True iff both streets are null or the Levenshtein distance divided by the length is &lt;= 0.2
     */
    public boolean isStreetMatchingByLevenshtein(final Address leftAddress, final Address rightAddress) {
        boolean addressesMatch = false;

        if (leftAddress.getStreet() == null && rightAddress.getStreet() == null) {
            addressesMatch = true;
        }

        if (leftAddress.getStreet() != null && rightAddress.getStreet() != null) {
            int levenshteinDistanceBetweenAddresses = levenshteinDistance.apply(leftAddress.getStreet(), rightAddress.getStreet());
            final int levenshteinToLengthRatio = levenshteinDistanceBetweenAddresses / (rightAddress.getStreet().length() + 1);
            addressesMatch = levenshteinToLengthRatio <= MINIMUM_LEVENSHTEIN_TO_LENGTH_RATION_TO_MATCH;
        }

        return addressesMatch;
    }
}
