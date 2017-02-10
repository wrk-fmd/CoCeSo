package at.wrk.geocode.autocomplete;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Abstract base class for autocomplete implemenations with (sorted) in-memory storage
 *
 * @param <T> The type of data stored
 */
public abstract class PreloadedAutocomplete<T> implements AutocompleteSupplier<T> {

  /**
   * The data store for all available entries
   */
  protected final NavigableMap<String, T> values;

  public PreloadedAutocomplete() {
    this(new TreeMap<>());
  }

  public PreloadedAutocomplete(NavigableMap<String, T> values) {
    this.values = values;
  }

  @Override
  public Stream<T> getStart(String filter) {
    String to = filter.substring(0, filter.length() - 1) + (char) (filter.charAt(filter.length() - 1) + 1);
    return values.subMap(filter, to).values().stream();
  }

  @Override
  public Stream<T> getContaining(String filter, Integer max) {
    if (max != null && max <= 0) {
      return Stream.empty();
    }

    Stream<T> filtered = values.entrySet().stream().filter(e -> e.getKey().indexOf(filter) > 0).map(Map.Entry::getValue);
    return max == null ? filtered : filtered.limit(max);
  }

}
