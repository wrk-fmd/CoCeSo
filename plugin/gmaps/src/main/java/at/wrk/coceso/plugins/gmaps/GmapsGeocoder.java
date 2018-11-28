package at.wrk.coceso.plugins.gmaps;

import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.IAddressNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class GmapsGeocoder extends AbstractGmapsGeocoder {
    private static final Logger LOG = LoggerFactory.getLogger(GmapsGeocoder.class);

    @Override
    protected String buildQueryString(Address address) {
        if (address.getStreet() == null) {
            LOG.trace("Address '{}' does not have an address set. Cannot build query for geocode.", address);
            return null;
        }

        String query = address.getStreet();
        IAddressNumber number = address.getNumber();
        if (number != null) {
            if (number.getFrom() != null) {
                query += " " + number.getFrom();
                if (number.getTo() != null) {
                    query += "-" + number.getTo();
                } else if (number.getLetter() != null) {
                    query += number.getLetter();
                }
            }
        }

        String city = address.buildCityLine();
        if (city != null) {
            query += ", " + city;
        }

        return query;
    }
}
