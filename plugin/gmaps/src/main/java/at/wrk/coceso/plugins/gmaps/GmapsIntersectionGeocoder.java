package at.wrk.coceso.plugins.gmaps;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class GmapsIntersectionGeocoder extends AbstractGmapsGeocoder {
    private static final Logger LOG = LoggerFactory.getLogger(GmapsIntersectionGeocoder.class);

    @Override
    public String buildQueryString(Address address) {
        if (address.getStreet() == null || address.getIntersection() == null) {
            LOG.trace("Address does not have a street or intersection set: '{}'. Cannot build query string.", address);
            return null;
        }

        String query = String.format("%s and %s", address.getStreet(), address.getIntersection());
        if (address.getCity() != null) {
            query += ", " + address.getCity();
        }

        return query;
    }

    @Override
    public ReverseResult<Address> reverse(LatLng coordinates) {
        return null;
    }

}
