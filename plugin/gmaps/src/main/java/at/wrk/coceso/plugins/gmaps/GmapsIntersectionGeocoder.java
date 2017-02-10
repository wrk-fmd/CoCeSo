package at.wrk.coceso.plugins.gmaps;

import at.wrk.geocode.address.Address;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class GmapsIntersectionGeocoder extends AbstractGmapsGeocoder {

  @Override
  public String buildQueryString(Address address) {
    if (address.getStreet() == null || address.getIntersection() == null) {
      return null;
    }

    String query = String.format("%s and %s", address.getStreet(), address.getIntersection());
    if (address.getCity() != null) {
      query += ", " + address.getCity();
    }
    return query;
  }

  @Override
  public ReverseResult<Address> reverse(LatLng coordinates) {
    return null;
  }

}
