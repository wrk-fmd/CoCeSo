package at.wrk.coceso.entity.helper;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class Address {

  private static final Logger LOG = Logger.getLogger(Address.class);

  private String title;
  private String street;
  private Integer numberFrom;
  private Integer numberTo;
  private String numberLetter;
  private String numberBlock;
  private String numberDetails;
  private Integer postCode;
  private String city;
  private String additional;

  /**
   * Parse info string
   *
   * @param info
   */
  public Address(String info) {
    if (info == null || info.trim().isEmpty()) {
      return;
    }
    String[] lines = info.split("\n");

    Pattern isStreet = Pattern.compile("^(\\w[\\w\\s\\-\\.]*?)"
            + "( ([1-9]\\d*(\\-([1-9]\\d*)|[a-zA-Z])?)?(/.*)?)?$", Pattern.UNICODE_CHARACTER_CLASS);
    Pattern isCity = Pattern.compile("^([1-9]\\d{3,4}) (\\w[\\w\\s\\-\\.]*)$", Pattern.UNICODE_CHARACTER_CLASS);
    Matcher street1, street2, city1, city2;

    switch (lines.length) {
      case 0:
        break;
      case 1:
        street1 = isStreet.matcher(lines[0].trim());
        if (street1.find(0)) {
          // First (and only) line represents street
          street = street1.group(1);
          setNumber(street1.group(2));
        } else if (!lines[0].trim().isEmpty()) {
          // Use as title (e.g. POI)
          title = lines[0].trim();
        }
        break;
      case 2:
        street1 = isStreet.matcher(lines[0].trim());
        street2 = isStreet.matcher(lines[1].trim());
        city1 = isCity.matcher(lines[1].trim());
        if (street1.find(0) && city1.find(0)) {
          // First line is street, second is city
          street = street1.group(1);
          setNumber(street1.group(2));
          postCode = Integer.parseInt(city1.group(1));
          city = city1.group(2);
        } else if (street2.find(0)) {
          // Second line is street
          title = lines[0].trim();
          street = street2.group(1);
          setNumber(street2.group(2));
        } else if (street1.find(0)) {
          // First line is street
          street = street1.group(1);
          setNumber(street1.group(2));
          additional = lines[1].trim();
        } else {
          title = lines[0].trim();
          additional = lines[1].trim();
        }
        break;
      default:
        street1 = isStreet.matcher(lines[0].trim());
        street2 = isStreet.matcher(lines[1].trim());
        city1 = isCity.matcher(lines[1].trim());
        city2 = isCity.matcher(lines[2].trim());
        int additionalStart;

        if (street2.find(0) && city2.find(0)) {
          // Second line is street, third is city
          title = lines[0].trim();
          street = street2.group(1);
          setNumber(street2.group(2));
          postCode = Integer.parseInt(city2.group(1));
          city = city2.group(2);
          additionalStart = 3;
        } else if (street1.find(0) && city1.find(0)) {
          // First line is street, second is city
          street = street1.group(1);
          setNumber(street1.group(2));
          postCode = Integer.parseInt(city1.group(1));
          city = city1.group(2);
          additionalStart = 2;
        } else if (street2.find(0)) {
          // Second line is street
          title = lines[0].trim();
          street = street2.group(1);
          setNumber(street2.group(2));
          additionalStart = 2;
        } else if (street1.find(0)) {
          // First line is street
          street = street1.group(1);
          setNumber(street1.group(2));
          additionalStart = 1;
        } else {
          title = lines[0].trim();
          additionalStart = 1;
        }
        for (int i = additionalStart; i < lines.length; i++) {
          additional = additional == null ? lines[i].trim() : additional + "\n" + lines[i].trim();
        }
        break;
    }
  }

  /**
   * Parse components from AddressInfo
   *
   * @param street
   * @param number
   * @param postCode
   * @param city
   */
  public Address(String street, String number, String postCode, String city) {
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
      setNumber(number.trim());
    }
  }

  public Address(String street, Integer numberFrom, Integer numberTo, Integer postCode) {
    this.street = street;
    this.numberFrom = numberFrom;
    this.numberTo = numberTo;
    this.postCode = postCode;
  }

  private void setNumber(String number) {
    if (number == null || number.trim().isEmpty()) {
      return;
    }

    number = number.trim();
    String[] components = number.split("/", 3);

    switch (components.length) {
      case 3:
        if (!components[2].trim().isEmpty()) {
          numberDetails = components[2];
        }
      case 2:
        if (!components[1].trim().isEmpty()) {
          numberBlock = components[1].trim();
        }
      case 1:
        Matcher matcher = Pattern.compile("([1-9]\\d*)(\\-([1-9]\\d*)|[a-zA-Z])?").matcher(components[0]);
        if (matcher.find()) {
          numberFrom = Integer.parseInt(matcher.group(1));
          String letter = matcher.group(2), to = matcher.group(3);
          if (to != null) {
            numberTo = Integer.parseInt(to);
            if (numberTo <= numberFrom || numberTo % 2 != numberFrom % 2) {
              // No valid interval
              numberTo = null;
            }
          } else if (letter != null) {
            numberLetter = letter.toUpperCase();
          }
        }
    }
  }

  public boolean exactMatch(Address b) {
    return (Objects.equals(numberFrom, b.numberFrom)
            && Objects.equals(numberTo, b.numberTo)
            && Objects.equals(numberLetter, b.numberLetter)
            && Objects.equals(numberBlock, b.numberBlock)
            && (b.postCode == null || Objects.equals(postCode, b.postCode)));
  }

  public boolean contains(Address b) {
    if (numberFrom == null || b.numberFrom == null) {
      // No number
      return Objects.equals(numberFrom, b.numberFrom);
    }

    if (numberTo == null && (b.numberTo != null || !Objects.equals(numberFrom, b.numberFrom))) {
      // Result is not a concatenated number, numbers are not matching
      return false;
    }

    if (numberTo != null) {
      // Result is concatenated
      if (b.numberFrom < numberFrom || b.numberFrom > numberTo || b.numberFrom % 2 != numberFrom % 2) {
        // Start number not in result interval
        return false;
      }
      if (b.numberTo != null && b.numberTo > numberTo) {
        // End number not in interval
        return false;
      }
    }

    if (numberLetter != null && !Objects.equals(numberLetter, b.numberLetter)) {
      // Found a not matching letter
      return false;
    }

    if (b.numberBlock != null && Objects.equals(numberBlock, b.numberBlock)) {
      // If looking for a block they have to match
      return false;
    }

    // Postal code has to match
    return (b.postCode == null || Objects.equals(postCode, b.postCode));
  }

  @Override
  public String toString() {
    String str = "";
    if (title != null && !title.isEmpty()) {
      str += title;
    }
    String streetString = searchString();
    if (streetString != null) {
      if (!str.isEmpty()) {
        str += "\n";
      }
      str += streetString;
      if (numberDetails != null) {
        if (numberBlock == null) {
          str += "/";
        }
        str += "/" + numberDetails;
      }
    }
    if (postCode != null || city != null) {
      if (!str.isEmpty()) {
        str += "\n";
      }
      str += (postCode + " " + city).trim();
    }
    if (additional != null) {
      if (!str.isEmpty()) {
        str += "\n";
      }
      str += additional;
    }
    return str;
  }

  public String searchString() {
    if (street == null || street.isEmpty()) {
      return null;
    }

    String str = street;
    if (numberFrom != null) {
      str += " " + numberFrom;
      if (numberTo != null) {
        str += "-" + numberTo;
      } else if (numberLetter != null) {
        str += numberLetter;
      }
    }
    if (numberBlock != null) {
      str += "/" + numberBlock;
    }
    return str;
  }

  public String getTitle() {
    return title;
  }

  public String getStreet() {
    return street;
  }

  public Integer getNumberFrom() {
    return numberFrom;
  }

  public Integer getNumberTo() {
    return numberTo;
  }

  public String getNumberLetter() {
    return numberLetter;
  }

  public String getNumberBlock() {
    return numberBlock;
  }

  public String getNumberDetails() {
    return numberDetails;
  }

  public Integer getPostCode() {
    return postCode;
  }

  public String getCity() {
    return city;
  }

  public String getAdditional() {
    return additional;
  }

}
