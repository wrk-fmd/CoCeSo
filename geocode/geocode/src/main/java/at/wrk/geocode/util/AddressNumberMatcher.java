package at.wrk.geocode.util;

import at.wrk.geocode.address.IAddressNumber;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddressNumberMatcher {
    /**
     * Check if found address number is an exact match.
     *
     * @param foundNumber The found number
     * @param searchInputNumber The number searched for
     * @return true iff the number matches.
     */
    public boolean exactMatch(final IAddressNumber foundNumber, final IAddressNumber searchInputNumber) {
        if (foundNumber == null && searchInputNumber == null) {
            return true;
        }
        if (foundNumber == null || searchInputNumber == null) {
            return false;
        }
        return (Objects.equals(foundNumber.getFrom(), searchInputNumber.getFrom())
                && Objects.equals(foundNumber.getTo(), searchInputNumber.getTo())
                && Objects.equals(foundNumber.getLetter(), searchInputNumber.getLetter())
                && Objects.equals(foundNumber.getBlock(), searchInputNumber.getBlock()));
    }

    /**
     * Non-symmetrical check if found number contains the number searched for
     *
     * @param foundNumber The found number
     * @param searchInputNumber The number searched for
     * @return
     */
    public boolean contains(final IAddressNumber foundNumber, final IAddressNumber searchInputNumber) {
        if (foundNumber == null && searchInputNumber == null) {
            // Both null: match
            return true;
        }
        if (foundNumber == null || searchInputNumber == null) {
            // Exactly one null: no match
            return false;
        }

        if (foundNumber.getFrom() == null && searchInputNumber.getFrom() == null) {
            // No number on both sides
            return true;
        }
        if (foundNumber.getFrom() == null || searchInputNumber.getFrom() == null) {
            // No number on one side
            return false;
        }

        if (foundNumber.getTo() == null && (searchInputNumber.getTo() != null || !Objects.equals(foundNumber.getFrom(), searchInputNumber.getFrom()))) {
            // Result is not a concatenated number, start numbers are not equal
            return false;
        }

        if (foundNumber.getTo() != null) {
            // Result is concatenated
            if (searchInputNumber.getFrom() < foundNumber.getFrom() || searchInputNumber.getFrom() > foundNumber.getTo() || searchInputNumber.getFrom() % 2 != foundNumber.getFrom() % 2) {
                // Start number not in result interval
                return false;
            }
            if (searchInputNumber.getTo() != null && searchInputNumber.getTo() > foundNumber.getTo()) {
                // End number not in interval
                return false;
            }
        }

        if (foundNumber.getLetter() != null && !Objects.equals(foundNumber.getLetter(), searchInputNumber.getLetter())) {
            // Found a not matching letter
            return false;
        }

        // Not looking for a block or blocks are equal
        return searchInputNumber.getBlock() == null || Objects.equals(foundNumber.getBlock(), searchInputNumber.getBlock());
    }
}
