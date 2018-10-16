package at.wrk.geocode.address;

import at.wrk.geocode.util.IntegerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation for the house number information of an address
 */
public class AddressNumber implements IAddressNumber {

  private static final Pattern PARTS = Pattern.compile("([1-9]\\d*)(\\-([1-9]\\d*)|[a-zA-Z])?");

  private final Integer from, to;
  private final String letter, block, details;

  protected AddressNumber() {
    from = null;
    to = null;
    letter = null;
    block = null;
    details = null;
  }

  public AddressNumber(String number) {
    Integer parsedFrom = null, parsedTo = null;
    String parsedLetter = null, parsedBlock = null, parsedDetails = null;

    if (!StringUtils.isBlank(number)) {
      number = number.trim();
      String[] components = number.split("/", 3);

      switch (components.length) {
        case 3:
          parsedDetails = StringUtils.trimToNull(components[2]);
        case 2:
          parsedBlock = StringUtils.trimToNull(components[1]);
        case 1:
          Matcher matcher = PARTS.matcher(components[0]);
          if (matcher.find()) {
            parsedFrom = IntegerUtils.parseInt(matcher.group(1)).orElse(null);
            if (parsedFrom != null) {
              parsedTo = IntegerUtils.parseInt(matcher.group(3)).orElse(null);
              if (parsedTo == null) {
                parsedLetter = StringUtils.upperCase(matcher.group(2));
              } else if (parsedTo <= parsedFrom || parsedTo % 2 != parsedFrom % 2) {
                // No valid interval
                parsedTo = null;
              }
            }
          }
      }
    }

    from = parsedFrom;
    to = parsedTo;
    letter = parsedLetter;
    block = parsedBlock;
    details = parsedDetails;
  }

  @Override
  public Integer getFrom() {
    return from;
  }

  @Override
  public Integer getTo() {
    return to;
  }

  @Override
  public String getLetter() {
    return letter;
  }

  @Override
  public String getBlock() {
    return block;
  }

  public String getDetails() {
    return details;
  }

  @Override
  public String toString() {
    return getText();
  }

  @Override
  public String getText() {
    String str = IAddressNumber.super.getText();

    if (str != null && details != null) {
      if (getBlock() == null) {
        str += "/";
      }
      str += "/" + details;
    }

    return str;
  }

}
