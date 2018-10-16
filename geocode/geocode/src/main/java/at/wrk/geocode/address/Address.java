package at.wrk.geocode.address;

public interface Address {

    String getStreet();

    String getIntersection();

    IAddressNumber getNumber();

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
}
