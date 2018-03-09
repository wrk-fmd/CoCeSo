package at.wrk.coceso.plugins.gmaps;

import at.wrk.geocode.address.Address;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class GmapsGeocoder extends AbstractGmapsGeocoder {

  @Override
  protected String buildQueryString(Address address) {
    if (address.getStreet() == null) {
      return null;
    }

    String query = address.getStreet();
    Address.Number number = address.getNumber();
    if (number != null) {
      if (number.getFrom() != null) {
        query += " " + number.getFrom();
        if (number.getTo() != null) {
          query += "-" + number.getTo();
        } else if (number.getLetter() != null) {
          query += number.getLetter();
        }
      }
    }

    String city = address.buildCityLine();
    if (city != null) {
      query += ", " + city;
    }

    return query;
  }

}
