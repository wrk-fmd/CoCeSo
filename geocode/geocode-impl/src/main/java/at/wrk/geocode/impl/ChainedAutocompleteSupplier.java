package at.wrk.geocode.impl;

import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Autocomplete implementation aggregating results from all other implementations
 */
@Component("ChainedAutocomplete")
public class ChainedAutocompleteSupplier implements AutocompleteSupplier<String> {

    private final List<AutocompleteSupplier<?>> autocomplete;

    @Autowired(required = false)
    public ChainedAutocompleteSupplier(final List<AutocompleteSupplier<?>> autocomplete) {
        // Exclude the chained streetname supplier here. To get rid of the hardcoded exclusion, the chained suppliers should implement a marker interface.
        this.autocomplete = autocomplete
                .stream()
                .filter(supplier -> !(supplier instanceof ChainedStreetnameAutocompleteSupplier))
                .collect(Collectors.toList());
    }

    @Override
    public String getString(String value) {
        return value;
    }

    @Override
    public Stream<String> getStart(String filter) {
        return autocomplete.stream().flatMap(a -> a.getStartString(filter));
    }

    @Override
    public Stream<String> getContaining(String filter, Integer max) {
        if (max != null && max <= 0) {
            return Stream.empty();
        }

        Stream<String> filtered = autocomplete.stream().flatMap(a -> a.getContainingString(filter, null));
        return max == null ? filtered : filtered.limit(max);
    }
}
