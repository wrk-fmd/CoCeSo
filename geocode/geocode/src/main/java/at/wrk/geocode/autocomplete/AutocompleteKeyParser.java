package at.wrk.geocode.autocomplete;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutocompleteKeyParser {

    /**
     * To search for a pattern the search must contain the hash in the first line, there must be input before the hash and the hash must be surrounded by blanks.
     */
    private static final Pattern INTERSECTION_SEARCH_PATTERN = Pattern.compile("^.*\\w.* # ");

    /**
     * Convert the given value to the format as it is used as key. This method should be used for creating the key of autocomplete database entries.
     *
     * @param value The unformatted input of the address.
     * @return The value converted to lower case and newlines replaced; null if value is null or blank
     */
    public static String formatAutocompleteKey(final String value) {
        String trimmedValue = StringUtils.trimToNull(value);
        return trimmedValue == null
                ? null
                : trimmedValue.toLowerCase(Locale.ROOT).replaceAll("\n", ", ");
    }

    public static Optional<String> getPrefixForIntersectionSearch(final String filter) {
        Matcher matcher = INTERSECTION_SEARCH_PATTERN.matcher(filter);

        Optional<String> prefix = Optional.empty();
        if (matcher.find()) {
            prefix = Optional.of(matcher.group());
        }

        return prefix;
    }
}
