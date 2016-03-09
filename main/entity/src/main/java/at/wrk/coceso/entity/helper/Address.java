package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.Point;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Address {

  protected String title;
  protected String street;
  protected Integer numberFrom;
  protected Integer numberTo;
  protected String numberLetter;
  protected String numberBlock;
  protected String numberDetails;
  protected Integer postCode;
  protected String city;
  protected String additional;

  protected Address() {
  }

  /**
   * Parse info string
   *
   * @param point
   */
  public Address(Point point) {
    if (point == null || point.getInfo() == null || point.getInfo().trim().isEmpty()) {
      return;
    }
    String[] lines = point.getInfo().trim().split("\n");

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
          parseNumber(street1.group(2));
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
          parseNumber(street1.group(2));
          postCode = Integer.parseInt(city1.group(1));
          city = city1.group(2);
        } else if (street2.find(0)) {
          // Second line is street
          title = lines[0].trim();
          street = street2.group(1);
          parseNumber(street2.group(2));
        } else if (street1.find(0)) {
          // First line is street
          street = street1.group(1);
          parseNumber(street1.group(2));
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
          parseNumber(street2.group(2));
          postCode = Integer.parseInt(city2.group(1));
          city = city2.group(2);
          additionalStart = 3;
        } else if (street1.find(0) && city1.find(0)) {
          // First line is street, second is city
          street = street1.group(1);
          parseNumber(street1.group(2));
          postCode = Integer.parseInt(city1.group(1));
          city = city1.group(2);
          additionalStart = 2;
        } else if (street2.find(0)) {
          // Second line is street
          title = lines[0].trim();
          street = street2.group(1);
          parseNumber(street2.group(2));
          additionalStart = 2;
        } else if (street1.find(0)) {
          // First line is street
          street = street1.group(1);
          parseNumber(street1.group(2));
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

  protected final void parseNumber(String number) {
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

  public boolean checkStreet(Address b) {
    if (street == null || b.street == null) {
      return false;
    }
    return StringUtils.getLevenshteinDistance(street, b.street) / (b.street.length() + 1) <= 0.2;
  }

  public boolean exactMatch(Address b) {
    return (Objects.equals(numberFrom, b.numberFrom)
            && Objects.equals(numberTo, b.numberTo)
            && Objects.equals(numberLetter, b.numberLetter)
            && Objects.equals(numberBlock, b.numberBlock)
            && (b.postCode == null || Objects.equals(postCode, b.postCode))
            && checkStreet(b));
  }

  public boolean contains(Address b) {
    if (!checkStreet(b)) {
      return false;
    }

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

    if (b.numberBlock != null && !Objects.equals(numberBlock, b.numberBlock)) {
      // If looking for a block they have to match
      return false;
    }

    if (b.postCode != null && !Objects.equals(postCode, b.postCode)) {
      // Postal code has to match
      return false;
    }

    return checkStreet(b);
  }

  @Override
  public String toString() {
    String str = "";
    if (title != null && !title.isEmpty()) {
      str += title;
    }
    String streetString = getStreetString();
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

  protected String getStreetString() {
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

  public Point getCoordinates() {
    return null;
  }

}
