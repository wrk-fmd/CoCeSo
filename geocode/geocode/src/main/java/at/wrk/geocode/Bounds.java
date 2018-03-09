package at.wrk.geocode;

/**
 * A class for handling WGS84 coordinate bounds
 */
public class Bounds {

  public final LatLng sw, ne;

  /**
   * Create an instance with the given coordinates
   *
   * @param sw South-west boundary
   * @param ne North-east boundary
   */
  public Bounds(LatLng sw, LatLng ne) {
    this.sw = sw;
    this.ne = ne;
  }

  /**
   * Check if the given coordinates are within the bounds
   *
   * @param coordinates
   * @return True iff within the boundaries, not considering wrapping at 180°
   */
  public boolean contains(LatLng coordinates) {
    return coordinates != null && coordinates.getLat() >= sw.getLat() && coordinates.getLat() <= ne.getLat()
        && coordinates.getLng() >= sw.getLng() && coordinates.getLng() <= ne.getLng();
  }

  @Override
  public String toString() {
    return String.format("(%s),(%s)", sw, ne);
  }

}
