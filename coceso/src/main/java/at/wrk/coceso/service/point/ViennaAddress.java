package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.helper.Address;
import java.util.Arrays;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class ViennaAddress extends Address {

  /**
   * Parse components from Point
   *
   * @param p
   */
  public ViennaAddress(Point p) {
    super(p);
  }

  /**
   * Parse components from AddressInfo
   *
   * @param street
   * @param number
   * @param postCode
   * @param city
   */
  private ViennaAddress(String street, String number, String postCode, String city) {
    if (street != null && !street.trim().isEmpty()) {
      this.street = street.trim();
    }

    if (postCode != null && !postCode.trim().isEmpty()) {
      try {
        this.postCode = Integer.parseInt(postCode.trim());
      } catch (NumberFormatException e) {
        this.postCode = null;
      }
    }

    if (city != null && !city.trim().isEmpty()) {
      this.city = city.trim();
    }

    if (number != null && !number.trim().isEmpty()) {
      parseNumber(number.trim());
    }
  }

  /**
   * Get coordinates for address
   *
   * @return Point with coordinates set
   */
  @Override
  public Point getCoordinates() {
    if (postCode != null && (postCode < 1010 || postCode >= 1240)) {
      return null;
    }

    String query = getStreetString();
    if (query == null) {
      return null;
    }
    AddressInfo info;
    try {
      info = new RestTemplate().getForObject("http://data.wien.gv.at/daten/OGDAddressService.svc/GetAddressInfo?crs=EPSG:4326&Address={query}", AddressInfo.class, query);
    } catch (RestClientException e) {
      return null;
    }

    if (info == null || info.count() <= 0) {
      return null;
    }

    if (info.count() > 1) {
      for (AddressInfo.Entry entry : info.getEntries()) {
        // First run: Look for exact match
        if (entry.getAddress().exactMatch(this)) {
          return entry.getPoint();
        }
      }

      for (AddressInfo.Entry entry : info.getEntries()) {
        // Second run: Look for bigger addresses containing the requested
        if (entry.getAddress().contains(this)) {
          return entry.getPoint();
        }
      }
    }

    // Only one entry or no match found, use lowest ranking
    return info.getEntries()[0].getAddress().checkStreet(this) ? info.getEntries()[0].getPoint() : null;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class AddressInfo {

    @JsonProperty("features")
    private final Entry[] entries;

    public AddressInfo(@JsonProperty("features") Entry[] entries) {
      Arrays.sort(entries);
      this.entries = entries;
    }

    public Entry[] getEntries() {
      return entries;
    }

    public int count() {
      return (entries == null) ? 0 : entries.length;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry implements Comparable<Entry> {

      private final double[] coordinates;
      private final Address address;
      private final Double ranking;

      @JsonCreator
      private Entry(@JsonProperty("geometry") Geometry geometry, @JsonProperty("properties") Properties properties) {
        this.coordinates = geometry != null ? geometry.coordinates : null;
        if (properties != null) {
          this.address = new ViennaAddress(properties.street, properties.number, properties.postCode, properties.city);
          this.ranking = properties.ranking;
        } else {
          this.address = null;
          this.ranking = null;
        }
      }

      public Point getPoint() {
        if (coordinates == null || coordinates.length < 2) {
          return null;
        }

        Point p = new Point();
        p.setLongitude(coordinates[0]);
        p.setLatitude(coordinates[1]);
        return p;
      }

      public Address getAddress() {
        return address;
      }

      public Double getRanking() {
        return ranking;
      }

      @Override
      public int compareTo(Entry b) {
        if (ranking == null) {
          return b.ranking == null ? 0 : 1;
        }
        if (b.ranking == null) {
          return -1;
        }
        if (Objects.equals(ranking, b.ranking)) {
          return 0;
        }
        return ranking < b.ranking ? -1 : 1;
      }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Geometry {

      @JsonProperty("coordinates")
      private double[] coordinates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Properties {

      @JsonProperty("StreetName")
      private String street;

      @JsonProperty("StreetNumber")
      private String number;

      @JsonProperty("PostalCode")
      private String postCode;

      @JsonProperty("Municipality")
      private String city;

      @JsonProperty("Ranking")
      private double ranking;
    }
  }
}
