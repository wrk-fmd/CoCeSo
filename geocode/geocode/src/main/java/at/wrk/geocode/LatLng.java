package at.wrk.geocode;

/**
 * A class for handling WGS84 coordinates
 */
public class LatLng {

  /**
   * The earth's equatorial radius in meters
   */
  private static final int RADIUS = 6371000;

  private final double lat, lng;

  /**
   * Default constructor for deserializing with Jackson
   */
  private LatLng() {
    this.lat = 0;
    this.lng = 0;
  }

  /**
   * Create an instance with the given coordinates
   *
   * @param lat Latitude in degrees (north &gt; 0, south &lt; 0)
   * @param lng Longitude in degrees (east &gt; 0, west &lt; 0)
   */
  public LatLng(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  /**
   * The latitude in degrees (north positive)
   *
   * @return
   */
  public double getLat() {
    return lat;
  }

  /**
   * The longitude in degrees (east positive)
   *
   * @return
   */
  public double getLng() {
    return lng;
  }

  /**
   * Distance to another coordinate object<br>
   * Fulfills symmetry, i.e. a.distance(b) == b.distance(a)
   *
   * @param that The other coordinates, must not be null
   * @return The (always positive) distance in meters
   */
  public int distance(LatLng that) {
    double lat1 = Math.toRadians(this.lat), lat2 = Math.toRadians(that.lat),
        a = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(Math.toRadians(this.lng - that.lng));
    return (int) Math.round(RADIUS * Math.acos(Math.min(a, 1)));
  }

  /**
   * Return the coordinate boundaries such that all coordinates outside are more than distance away
   *
   * @param distance
   * @return
   */
  public Bounds boundsForDistance(int distance) {
    double latDiffRad = distance / (double) RADIUS,
        latDiff = Math.toDegrees(latDiffRad),
        lngDiff = Math.toDegrees(Math.asin(Math.sin(latDiffRad) / Math.cos(Math.toRadians(lat)))),
        latMin = lat - latDiff,
        latMax = lat + latDiff,
        lngMin = lng - lngDiff,
        lngMax = lng + lngDiff;

    if (latMin <= -90) {
      // Special case: South pole included
      latMin = -90;
      lngMin = -180;
      lngMax = 180;
    }
    if (latMax >= 90) {
      // Special case: North pole included
      latMax = 90;
      lngMin = -180;
      lngMax = 180;
    }

    // Special cases: Wrapping around 180°-meridian, ignore some possibly matching entries
    // this will (probably) never happen in practice, since the 180°-meridian is mostly in the middle of the ocean
    lngMin = Math.max(lngMin, -180);
    lngMax = Math.min(lngMax, 180);

    return new Bounds(new LatLng(latMin, lngMin), new LatLng(latMax, lngMax));
  }

  @Override
  public String toString() {
    return String.format("%f,%f", lat, lng);
  }

}
