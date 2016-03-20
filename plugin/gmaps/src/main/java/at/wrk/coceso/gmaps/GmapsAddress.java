package at.wrk.coceso.gmaps;

import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.helper.Address;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GmapsAddress extends Address {

  private static final Logger LOG = LoggerFactory.getLogger(GmapsAddress.class);

  private final GeoApiContext context;

  /**
   * Parse components from Point
   *
   * @param p
   */
  public GmapsAddress(Point p, GeoApiContext context) {
    super(p);
    this.context = context;
  }

  /**
   * Get coordinates for address
   *
   * @return Point with coordinates set
   */
  @Override
  public Point getCoordinates() {
    if (street == null || intersection == null) {
      return null;
    }

    String request = String.format("%s and %s", street, intersection);
    if (postCode != null || city != null) {
      request += ", " + (postCode + " " + city).trim();
    }

    try {
      GeocodingResult[] results = GeocodingApi.geocode(context, request).await();
      if (results.length > 0) {
        LatLng latLng = results[0].geometry.location;
        Point coordinates = new Point();
        coordinates.setLatitude(latLng.lat);
        coordinates.setLongitude(latLng.lng);
        return coordinates;
      }
    } catch (Exception ex) {
      LOG.info("Error getting coordinats for '{}'", request, ex);
    }

    return null;
  }

}
