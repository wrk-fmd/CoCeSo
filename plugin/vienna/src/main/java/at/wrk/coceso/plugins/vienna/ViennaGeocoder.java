package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Order(40)
public class ViennaGeocoder implements Geocoder<Address> {

  private static final Logger LOG = LoggerFactory.getLogger(ViennaGeocoder.class);
  private static final String GEOCODE_URL = "https://data.wien.gv.at/daten/OGDAddressService.svc/GetAddressInfo?CRS=EPSG:4326&Address={query}";
  private static final String REVERSE_URL = "https://data.wien.gv.at/daten/OGDAddressService.svc/ReverseGeocode?CRS=EPSG:4326&type=A3:8012&location={lng},{lat}";
  private static final Bounds BOUNDS = new Bounds(new LatLng(48.1183, 16.1827), new LatLng(48.3231, 16.5787));

  @Override
  public LatLng geocode(Address address) {
    if (address.getStreet() == null) {
      // Geocoding only possible if at least a street name is specified
      return null;
    }

    if (address.getPostCode() != null && (address.getPostCode() < 1010 || address.getPostCode() >= 1240)) {
      // Postcode is not in Vienna
      return null;
    }

    if (address.getCity() != null && !address.getCity().toLowerCase().startsWith("wien")) {
      // City is not Vienna
      return null;
    }

    String query = buildQueryString(address);
    AddressInfoList infos;
    try {
      infos = new RestTemplate().getForObject(GEOCODE_URL, AddressInfoList.class, query);
    } catch (RestClientException e) {
      return null;
    }

    if (infos == null || infos.count() <= 0) {
      return null;
    }

    if (infos.count() > 1) {
      for (AddressInfoEntry entry : infos.getEntries()) {
        // First run: Look for exact match
        if (Address.matches(entry.getAddress(), address, true)) {
          return entry.getCoordinates();
        }
      }

      for (AddressInfoEntry entry : infos.getEntries()) {
        // Second run: Look for bigger addresses containing the requested
        if (Address.matches(entry.getAddress(), address, false)) {
          return entry.getCoordinates();
        }
      }
    }

    // Only one entry or no match found, use lowest ranking
    return Address.checkStreet(infos.getEntries()[0].getAddress(), address) ? infos.getEntries()[0].getCoordinates() : null;
  }

  @Override
  public ReverseResult<Address> reverse(LatLng coordinates) {
    if (!BOUNDS.contains(coordinates)) {
      return null;
    }

    try {
      AddressInfoList infos = new RestTemplate().getForObject(REVERSE_URL, AddressInfoList.class, coordinates.getLng(), coordinates.getLat());
      if (infos == null || infos.count() <= 0) {
        return null;
      }
      AddressInfoEntry entry = infos.getEntries()[0];
      int dist = coordinates.distance(entry.getCoordinates());
      LOG.debug("Found address '{}' {} meters away with data.wien.gv.at", entry.getAddress(), dist);
      return new ReverseResult<>(dist, entry.getAddress(), entry.getCoordinates());
    } catch (RestClientException ex) {
      LOG.info("Error getting address for '{}'", coordinates, ex);
    }

    return null;
  }

  private String buildQueryString(Address address) {
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
        if (number.getBlock() != null) {
          query += "/" + number.getBlock();
        }
      }
    }

    return query;
  }

}
