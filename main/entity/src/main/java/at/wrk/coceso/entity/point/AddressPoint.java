package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.AddressNumber;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A Point representing an address
 */
@Configurable
class AddressPoint implements Point, Address {

  // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
  @Autowired
  @Qualifier("ChainedGeocoder")
  private Geocoder<Address> addressGeocoder;

  private static final Pattern isStreet = Pattern.compile("^(\\w[\\w\\s\\-\\.]*?)"
      + "( ([1-9]\\d*(\\-([1-9]\\d*)|[a-zA-Z])?)?(/.*)?)?( # (\\w[\\w\\s\\-\\.]*))?$", Pattern.UNICODE_CHARACTER_CLASS);
  private static final Pattern isCity = Pattern.compile("^(([1-9]\\d{3,4}) )?(\\w[\\w\\s\\-\\.]*)$", Pattern.UNICODE_CHARACTER_CLASS);

  private final String title, street, intersection, city, additional;
  private final Integer postCode;
  private LatLng coordinates;

  @JsonDeserialize(as = AddressNumber.class)
  private final Number number;

  private AddressPoint() {
    title = null;
    street = null;
    intersection = null;
    number = null;
    postCode = null;
    city = null;
    additional = null;
  }

  /**
   * Parse address string
   *
   * @param str
   */
  public AddressPoint(String str) {
    String[] parsedData = null;
    String parsedTitle = null, parsedAdditional = null;

    if (!StringUtils.isBlank(str)) {
      String[] lines = str.trim().split("\n");
      for (int i = 0; i < lines.length; i++) {
        lines[i] = lines[i].trim();
      }

      Matcher street0, street1, city1, city2;

      switch (lines.length) {
        case 0:
          break;
        case 1:
          street0 = isStreet.matcher(lines[0]);
          if (street0.find(0)) {
            // First (and only) line represents street
            parsedData = getFromRegex(street0, null);
          } else if (StringUtils.isNotBlank(lines[0])) {
            // Use as title (e.g. POI)
            parsedTitle = lines[0];
          }
          break;
        case 2:
          street0 = isStreet.matcher(lines[0]);
          street1 = isStreet.matcher(lines[1]);
          city1 = isCity.matcher(lines[1]);
          if (street0.find(0) && city1.find(0)) {
            // First line is street, second is city
            parsedData = getFromRegex(street0, city1);
          } else if (street1.find(0)) {
            // Second line is street
            parsedTitle = lines[0];
            parsedData = getFromRegex(street1, null);
          } else if (street0.find(0)) {
            // First line is street
            parsedData = getFromRegex(street0, null);
            parsedAdditional = lines[1];
          } else {
            parsedTitle = lines[0];
            parsedAdditional = lines[1];
          }
          break;
        default:
          street0 = isStreet.matcher(lines[0]);
          street1 = isStreet.matcher(lines[1]);
          city1 = isCity.matcher(lines[1]);
          city2 = isCity.matcher(lines[2]);
          int additionalStart;

          if (street1.find(0) && city2.find(0)) {
            // Second line is street, third is city
            parsedTitle = lines[0];
            parsedData = getFromRegex(street1, city2);
            additionalStart = 3;
          } else if (street0.find(0) && city1.find(0)) {
            // First line is street, second is city
            parsedData = getFromRegex(street0, city1);
            additionalStart = 2;
          } else if (street1.find(0)) {
            // Second line is street
            parsedTitle = lines[0];
            parsedData = getFromRegex(street1, null);
            additionalStart = 2;
          } else if (street0.find(0)) {
            // First line is street
            parsedData = getFromRegex(street0, null);
            additionalStart = 1;
          } else {
            parsedTitle = lines[0];
            additionalStart = 1;
          }

          parsedAdditional = String.join("\n", Arrays.copyOfRange(lines, additionalStart, lines.length));
          break;
      }
    }

    if (parsedData == null) {
      street = null;
      number = new AddressNumber(null);
      intersection = null;
      postCode = null;
      city = null;
    } else {
      street = StringUtils.trimToNull(parsedData[0]);
      number = new AddressNumber(parsedData[1]);
      intersection = StringUtils.trimToNull(parsedData[2]);
      postCode = Address.parseInt(parsedData[3]);
      city = StringUtils.trimToNull(parsedData[4]);
    }

    title = StringUtils.trimToNull(parsedTitle);
    additional = StringUtils.trimToNull(parsedAdditional);
  }

  private String[] getFromRegex(Matcher street, Matcher city) {
    String[] parsedData = new String[5];
    if (street != null) {
      parsedData[0] = street.group(1);
      parsedData[1] = street.group(3);
      parsedData[2] = street.group(8);
    }
    if (city != null) {
      parsedData[3] = city.group(2);
      parsedData[4] = city.group(3);
    }
    return parsedData;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  @Override
  public String getStreet() {
    return street;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  @Override
  public String getIntersection() {
    return intersection;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  @Override
  public Number getNumber() {
    return number;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  @Override
  public Integer getPostCode() {
    return postCode;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  @Override
  public String getCity() {
    return city;
  }

  @JsonView(JsonViews.PointMinimal.class)
  @Override
  public String getInfo() {
    return Address.super.getInfo();
  }

  @Override
  public String getInfo(String newline) {
    String str = "";
    if (title != null) {
      str += title;
    }

    String address = Address.super.getInfo(newline);
    if (!address.isEmpty() && !str.isEmpty()) {
      str += newline;
    }
    str += address;

    if (additional != null) {
      if (!str.isEmpty()) {
        str += newline;
      }
      str += additional;
    }
    return str;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointMinimal.class})
  @Override
  public LatLng getCoordinates() {
    fill();
    return coordinates;
  }

  private void fill() {
    if (coordinates == null && !isEmpty()) {
      coordinates = addressGeocoder.geocode(this);
    }
  }

  @Override
  public boolean isEmpty() {
    return StringUtils.isEmpty(this.title) && StringUtils.isEmpty(this.street);
  }

  @Override
  public String toString() {
    return getInfo(", ");
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode(this.title);
    hash = 59 * hash + Objects.hashCode(this.street);
    hash = 59 * hash + Objects.hashCode(this.intersection);
    hash = 59 * hash + Objects.hashCode(this.number);
    hash = 59 * hash + Objects.hashCode(this.postCode);
    hash = 59 * hash + Objects.hashCode(this.city);
    hash = 59 * hash + Objects.hashCode(this.additional);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AddressPoint other = (AddressPoint) obj;
    return Objects.equals(this.title, other.title)
        && Objects.equals(this.street, other.street)
        && Objects.equals(this.intersection, other.intersection)
        && Objects.equals(this.number, other.number)
        && Objects.equals(this.postCode, other.postCode)
        && Objects.equals(this.city, other.city)
        && Objects.equals(this.additional, other.additional);
  }

}
