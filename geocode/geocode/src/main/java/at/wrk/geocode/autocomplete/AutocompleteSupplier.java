package at.wrk.geocode.autocomplete;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Interface for all classes that can supply autocomplete data
 *
 * @param <T> The type of the returned data, e.g. String or Address
 */
public interface AutocompleteSupplier<T> {

  /**
   * Get a string representation of the data
   *
   * @param value, non-null
   * @return Non-null string representation
   */
  String getString(T value);

  /**
   * Get a stream of all entries starting with the query string
   *
   * @param filter The query string in lower case, must not be null
   * @return A (possibly empty) Stream of items
   */
  Stream<T> getStart(String filter);

  /**
   * Get a stream of at most max entries containing the query string
   *
   * @param filter The query string in lower case, must not be null
   * @param max The maximum number of returned entries, or null if unlimited
   * @return A (possibly empty) Stream of items
   */
  Stream<T> getContaining(String filter, Integer max);

  /**
   * Get a collection of all entries starting with the query string
   *
   * @param filter The query string in lower case, must not be null
   * @return A (possibly empty) Collection of items
   */
  default Collection<T> getStartCollection(String filter) {
    return getStart(filter).collect(Collectors.toList());
  }

  /**
   * Get a collection of at most max entries containing the query string
   *
   * @param filter The query string in lower case, must not be null
   * @param max The maximum number of returned entries, or null if unlimited
   * @return A (possibly empty) Collection of items
   */
  default Collection<T> getContainingCollection(String filter, Integer max) {
    return getContaining(filter, max).collect(Collectors.toList());
  }

  /**
   * Get a stream of all entries converted to String starting with the query string
   *
   * @param filter The query string in lower case, must not be null
   * @return A (possibly empty) Stream of items converted to String using #getString
   */
  default Stream<String> getStartString(String filter) {
    return getStart(filter).map(v -> getString(v));
  }

  /**
   * Get a stream of at most max entries converted to String containing the query string
   *
   * @param filter The query string in lower case, must not be null
   * @param max The maximum number of returned entries, or null if unlimited
   * @return A (possibly empty) Stream of items converted to String using #getString
   */
  default Stream<String> getContainingString(String filter, Integer max) {
    return getContaining(filter, max).map(v -> getString(v));
  }

  /**
   * Get a collection of all entries converted to String starting with the query string
   *
   * @param filter The query string in lower case, must not be null
   * @return A (possibly empty) Collection of items converted to String using #getString
   */
  default Collection<String> getStartStringCollection(String filter) {
    return getStartString(filter).collect(Collectors.toList());
  }

  /**
   * Get a collection of at most max entries converted to String containing the query string
   *
   * @param filter The query string in lower case, must not be null
   * @param max The maximum number of returned entries, or null if unlimited
   * @return A (possibly empty) Collection of items converted to String using #getString
   */
  default Collection<String> getContainingStringCollection(String filter, Integer max) {
    return getContainingString(filter, max).collect(Collectors.toList());
  }

  /**
   * Convert the given filter value to the format as it is used as key
   *
   * @param value
   * @return The value converted to lower case and newlines replaced; null if value is null or blank
   */
  static String getKey(String value) {
    value = StringUtils.trimToNull(value);
    return value == null ? null : value.toLowerCase().replaceAll("\n", ", ");
  }

}
