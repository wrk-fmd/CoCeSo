package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.AddressNumber;
import at.wrk.geocode.address.IAddressNumber;
import at.wrk.geocode.util.IntegerUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

class AddressInfoEntry implements Comparable<AddressInfoEntry> {

  private final LatLng coordinates;
  private final ViennaAddress address;
  private final Double ranking;

  @JsonCreator
  private AddressInfoEntry(@JsonProperty("geometry") Coordinates coordinates, @JsonProperty("properties") ViennaAddress address) {
    this.coordinates = coordinates == null ? null : coordinates.latLng;
    this.address = address;

    if (address != null) {
      this.ranking = address.ranking;
    } else {
      this.ranking = null;
    }
  }

  public LatLng getCoordinates() {
    return coordinates;
  }

  public Address getAddress() {
    return address;
  }

  public Double getRanking() {
    return ranking;
  }

  @Override
  public int compareTo(AddressInfoEntry b) {
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

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class Coordinates {

    private final LatLng latLng;

    public Coordinates(@JsonProperty("coordinates") double[] coordinates) {
      this.latLng = (coordinates != null && coordinates.length >= 2) ? new LatLng(coordinates[1], coordinates[0]) : null;
    }

  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class ViennaAddress implements Address {

    private final String street, city;
    private final Integer postCode;
    private final IAddressNumber number;
    private final double ranking;

    ViennaAddress(@JsonProperty("StreetName") String street, @JsonProperty("StreetNumber") String number,
        @JsonProperty("PostalCode") String postCode, @JsonProperty("Municipality") String city, @JsonProperty("Ranking") double ranking) {
      this.street = StringUtils.trimToNull(street);
      this.city = StringUtils.trimToNull(city);
      this.number = new AddressNumber(number);
      this.postCode = IntegerUtils.parseInt(postCode).orElse(null);
      this.ranking = ranking;
    }

    @Override
    public String getStreet() {
      return street;
    }

    @Override
    public String getIntersection() {
      return null;
    }

    @Override
    public IAddressNumber getNumber() {
      return number;
    }

    @Override
    public Integer getPostCode() {
      return postCode;
    }

    @Override
    public String getCity() {
      return city;
    }

    @Override
    public String toString() {
      return getInfo(", ");
    }

  }

}
