package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.helper.Address;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Objects;

public class ViennaLocate implements ILocate {

  private final static Logger LOG = Logger.getLogger(ViennaLocate.class);
  private final RestTemplate restTemplate;

  public ViennaLocate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public boolean locate(Point p) {
    try {
      AddressInfo.Entry entry = getCoordinatesForAddress(new Address(p.getInfo()));
      if (entry != null && entry.getCoordinates().length >= 2) {
        p.setLongitude(entry.getCoordinates()[0]);
        p.setLatitude(entry.getCoordinates()[1]);
        return true;
      }
    } catch (Exception e) {
      LOG.error(e.getClass().toString(), e);
    }

    return false;
  }

  private AddressInfo.Entry getCoordinatesForAddress(Address address) {
    String query = address.searchString();
    if (query == null) {
      return null;
    }
    AddressInfo info = restTemplate.getForObject("http://data.wien.gv.at/daten/OGDAddressService.svc/GetAddressInfo?crs=EPSG:4326&Address={query}", AddressInfo.class, query);

    if (info == null || info.count() <= 0) {
      return null;
    }

    if (info.count() > 1) {
      for (AddressInfo.Entry entry : info.getEntries()) {
        // First run: Look for exact match
        if (entry.getAddress().exactMatch(address)) {
          return entry;
        }
      }

      for (AddressInfo.Entry entry : info.getEntries()) {
        // Second run: Look for bigger addresses containing the requested
        if (entry.getAddress().contains(address)) {
          return entry;
        }
      }
    }

    // Only one entry or no match found, use lowest ranking
    return info.getEntries()[0];
  }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class AddressInfo {

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
        this.address = new Address(properties.street, properties.number, properties.postCode, properties.city);
        this.ranking = properties.ranking;
      } else {
        this.address = null;
        this.ranking = null;
      }
    }

    public double[] getCoordinates() {
      return coordinates;
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

    @JsonProperty("Adresse")
    private String address;

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
