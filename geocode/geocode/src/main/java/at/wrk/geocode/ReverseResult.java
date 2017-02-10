package at.wrk.geocode;

/**
 * Container for the result of a reverse geocoding operation
 *
 * @param <T> The type of the result, e.g. Address
 */
public class ReverseResult<T> implements Comparable<ReverseResult> {

  public final int dist;
  public final T result;
  public final LatLng coordinates;

  /**
   * Creates a new ReverseResult instance
   *
   * @param dist The distance of the result to the search coordinates
   * @param result The result itself, e.g. an Address object
   * @param coordinates The actual coordinates of the result (as opposed to the search coordinates)
   */
  public ReverseResult(int dist, T result, LatLng coordinates) {
    this.dist = dist;
    this.result = result;
    this.coordinates = coordinates;
  }

  @Override
  public int compareTo(ReverseResult that) {
    return this.dist - that.dist;
  }

}
