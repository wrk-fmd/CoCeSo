package at.wrk.geocode.impl;

import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Autocomplete implementation aggregating results from all other implementations
 */
@Component("ChainedAutocomplete")
public class ChainedAutocompleteSupplier implements AutocompleteSupplier<String> {

  private final List<AutocompleteSupplier<?>> autocomplete;

  public ChainedAutocompleteSupplier() {
    autocomplete = Collections.emptyList();
  }

  @Autowired(required = false)
  public ChainedAutocompleteSupplier(List<AutocompleteSupplier<?>> autocomplete) {
    this.autocomplete = autocomplete;
  }

  public ChainedAutocompleteSupplier(AutocompleteSupplier<?>... autocomplete) {
    this.autocomplete = Arrays.asList(autocomplete);
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
