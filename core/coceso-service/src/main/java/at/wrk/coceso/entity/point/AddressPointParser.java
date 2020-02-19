package at.wrk.coceso.entity.point;

import at.wrk.geocode.address.AddressNumber;
import at.wrk.geocode.util.IntegerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressPointParser {

    private static final Pattern STREET_PATTERN = Pattern.compile("^(\\w[\\w\\s\\-.]*?)"
            + "( ([1-9]\\d*(-([1-9]\\d*)|[a-zA-Z])?)?(/.*)?)?( # (\\w[\\w\\s\\-.]*))?$", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern CITY_PATTERN = Pattern.compile("^(([1-9]\\d{3,4}) )?(\\w[\\w\\s\\-.]*)$", Pattern.UNICODE_CHARACTER_CLASS);

    public static AddressPoint parseFromString(final String addressString) {
        String[] parsedData = null;
        String parsedTitle = null, parsedAdditional = null;

        if (!StringUtils.isBlank(addressString)) {
            String[] lines = addressString.trim().split("\n");
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i].trim();
            }

            Matcher street0, street1, city1, city2;

            switch (lines.length) {
                case 0:
                    break;
                case 1:
                    street0 = STREET_PATTERN.matcher(lines[0]);
                    if (street0.find(0)) {
                        // First (and only) line represents street
                        parsedData = getFromRegex(street0, null);
                    } else if (StringUtils.isNotBlank(lines[0])) {
                        // Use as title (e.g. POI)
                        parsedTitle = lines[0];
                    }
                    break;
                case 2:
                    street0 = STREET_PATTERN.matcher(lines[0]);
                    street1 = STREET_PATTERN.matcher(lines[1]);
                    city1 = CITY_PATTERN.matcher(lines[1]);
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
                    street0 = STREET_PATTERN.matcher(lines[0]);
                    street1 = STREET_PATTERN.matcher(lines[1]);
                    city1 = CITY_PATTERN.matcher(lines[1]);
                    city2 = CITY_PATTERN.matcher(lines[2]);
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

        String title = StringUtils.trimToNull(parsedTitle);
        String additional = StringUtils.trimToNull(parsedAdditional);

        AddressPoint addressPoint;
        if (parsedData == null) {
            addressPoint = new AddressPoint(
                    title,
                    null,
                    null,
                    null,
                    additional,
                    null,
                    AddressNumber.createEmpty()
            );
        } else {
            String street = StringUtils.trimToNull(parsedData[0]);
            AddressNumber number = new AddressNumber(parsedData[1]);
            String intersection = StringUtils.trimToNull(parsedData[2]);
            Integer postCode = IntegerUtils.parseInt(parsedData[3]).orElse(null);
            String city = StringUtils.trimToNull(parsedData[4]);
            addressPoint = new AddressPoint(
                    title,
                    street,
                    intersection,
                    city,
                    additional,
                    postCode,
                    number
            );
        }

        return addressPoint;
    }

    private static String[] getFromRegex(Matcher street, Matcher city) {
        String[] parsedData = new String[5];
        if (street != null) {
            parsedData[0] = street.group(1);
            parsedData[1] = street.group(2);
            parsedData[2] = street.group(8);
        }
        if (city != null) {
            parsedData[3] = city.group(2);
            parsedData[4] = city.group(3);
        }
        return parsedData;
    }
}
