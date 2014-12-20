package at.wrk.coceso.entity.helper;

import java.util.Arrays;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressInfo {

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
