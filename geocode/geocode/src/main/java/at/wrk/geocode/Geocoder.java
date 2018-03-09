package at.wrk.geocode;

/**
 * Interface for an object capable of geocoding and reverse geocoding
 *
 * @param <T> The type of the data to geocode, e.g. Address
 */
public interface Geocoder<T> {

  /**
   * Geocode the given data to coordinates
   *
   * @param search The search data, must not be null
   * @return The coordinates, or null if none were found
   */
  LatLng geocode(T search);

  /**
   * Reverse geocode the given coordinates
   *
   * @param coordinates The coordinates to reverse geocode, must not be null
   * @return The result, or null if none was found
   */
  ReverseResult<T> reverse(LatLng coordinates);

}
