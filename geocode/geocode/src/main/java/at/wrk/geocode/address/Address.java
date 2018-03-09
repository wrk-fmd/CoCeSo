package at.wrk.geocode.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public interface Address {

  String getStreet();

  String getIntersection();

  Number getNumber();

  Integer getPostCode();

  String getCity();

  /**
   * Builds an info string containing all stored elements with lines delimited by \n
   *
   * @return
   */
  default String getInfo() {
    return getInfo("\n");
  }

  /**
   * Builds an info string containing all stored elements
   *
   * @param newline A character sequence for delimiting the lines
   * @return
   */
  default String getInfo(String newline) {
    String str = buildStreetLine(),
        city = buildCityLine();
    if (city != null) {
      str = str == null ? city : str + newline + city;
    }
    return str == null ? "" : str;
  }

  /**
   * Builds a line containing the streetname, the number and the intersection, if given
   *
   * @return
   */
  default String buildStreetLine() {
    String str = getStreet();
    if (str == null) {
      return null;
    }

    String numberString = getNumber() == null ? null : getNumber().getText();
    if (numberString != null) {
      str += " " + numberString;
    }

    if (getIntersection() != null) {
      str += " # " + getIntersection();
    }

    return str;
  }

  /**
   * Builds a line containing the post code and the city, if given
   *
   * @return
   */
  default String buildCityLine() {
    String line = getCity();
    if (getPostCode() != null) {
      line = line == null ? getPostCode().toString() : getPostCode().toString() + " " + line;
    }
    return line;
  }

  /**
   * Non-symmetrical check if found address is a match
   *
   * @param a The found address
   * @param b The address searched for
   * @param exact Search for an exact match of numbers
   * @return True iff post code of b matches (if given), street is a close match (as given by #checkStreet) and number
   * of a matches the one of b exactly/is contained within range given in b
   */
  static boolean matches(Address a, Address b, boolean exact) {
    if (a == null || b == null) {
      return false;
    }

    return (exact ? Number.exactMatch(a.getNumber(), b.getNumber()) : Number.contains(a.getNumber(), b.getNumber()))
        && (b.getPostCode() == null || Objects.equals(a.getPostCode(), b.getPostCode()))
        && checkStreet(a, b);
  }

  /**
   * Check if both streetnames match
   *
   * @param a
   * @param b
   * @return True iff both streets are null or the Levenshtein distance divided by the length is &lt;= 0.2
   */
  static boolean checkStreet(Address a, Address b) {
    if (a.getStreet() == null && b.getStreet() == null) {
      return true;
    }
    if (a.getStreet() == null || b.getStreet() == null) {
      return false;
    }
    return StringUtils.getLevenshteinDistance(a.getStreet(), b.getStreet()) / (b.getStreet().length() + 1) <= 0.2;
  }

  /**
   * Helper function to parse an Integer
   *
   * @param str
   * @return The integer value of str, or null if not a valid integer
   */
  static Integer parseInt(String str) {
    if (StringUtils.isBlank(str)) {
      return null;
    }
    try {
      return Integer.parseInt(str.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  static interface Number {

    Integer getFrom();

    Integer getTo();

    String getLetter();

    String getBlock();

    @JsonIgnore
    default String getText() {
      if (getFrom() == null) {
        return null;
      }

      String str = "" + getFrom();
      if (getTo() != null) {
        str += "-" + getTo();
      } else if (getLetter() != null) {
        str += getLetter();
      }

      if (getBlock() != null) {
        str += "/" + getBlock();
      }

      return str;
    }

    /**
     * Check if found number is an exact match
     *
     * @param a The found number
     * @param b The number searched for
     * @return
     */
    static boolean exactMatch(Number a, Number b) {
      if (a == null && b == null) {
        return true;
      }
      if (a == null || b == null) {
        return false;
      }
      return (Objects.equals(a.getFrom(), b.getFrom())
          && Objects.equals(a.getTo(), b.getTo())
          && Objects.equals(a.getLetter(), b.getLetter())
          && Objects.equals(a.getBlock(), b.getBlock()));
    }

    /**
     * Non-symmetrical check if found number contains the number searched for
     *
     * @param a The found number
     * @param b The number searched for
     * @return
     */
    static boolean contains(Number a, Number b) {
      if (a == null && b == null) {
        // Both null: match
        return true;
      }
      if (a == null || b == null) {
        // Exactly one null: no match
        return false;
      }

      if (a.getFrom() == null && b.getFrom() == null) {
        // No number on both sides
        return true;
      }
      if (a.getFrom() == null || b.getFrom() == null) {
        // No number on one side
        return false;
      }

      if (a.getTo() == null && (b.getTo() != null || !Objects.equals(a.getFrom(), b.getFrom()))) {
        // Result is not a concatenated number, start numbers are not equal
        return false;
      }

      if (a.getTo() != null) {
        // Result is concatenated
        if (b.getFrom() < a.getFrom() || b.getFrom() > a.getTo() || b.getFrom() % 2 != a.getFrom() % 2) {
          // Start number not in result interval
          return false;
        }
        if (b.getTo() != null && b.getTo() > a.getTo()) {
          // End number not in interval
          return false;
        }
      }

      if (a.getLetter() != null && !Objects.equals(a.getLetter(), b.getLetter())) {
        // Found a not matching letter
        return false;
      }

      // Not looking for a block or blocks are equal
      return b.getBlock() == null || Objects.equals(a.getBlock(), b.getBlock());
    }

  }
}
