package at.wrk.coceso.plugins.gmaps;

import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.AddressNumber;
import at.wrk.geocode.address.IAddressNumber;
import at.wrk.geocode.address.ImmutableAddress;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractGmapsGeocoder implements Geocoder<ImmutableAddress> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGmapsGeocoder.class);

    @Autowired
    private GmapsWrapper gmapsWrapper;

    @Override
    public LatLng geocode(ImmutableAddress address) {
        if (!gmapsWrapper.isActive()) {
            LOG.trace("Google Maps wrapper is not active");
            return null;
        }

        String query = buildQueryString(address);
        if (query == null) {
            LOG.trace("Cannot query null-query. Address input was: '{}'.", address);
            return null;
        }

        LOG.debug("Querying Google Maps for '{}'", query);
        try {
            GeocodingResult[] results = gmapsWrapper.geocode(query);
            if (results.length > 0) {
                return new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
            }
        } catch (Exception ex) {
            LOG.info("Error getting coordinates for address query: " + query, ex);
        }

        return null;
    }

    @Override
    public ReverseResult<ImmutableAddress> reverse(LatLng coordinates) {
        if (!gmapsWrapper.isActive()) {
            return null;
        }

        LOG.debug("Reverse geocoding with Google Maps for ({})", coordinates);
        try {
            GeocodingResult[] results = gmapsWrapper.reverseGeocode(coordinates.getLat(), coordinates.getLng());
            if (results.length > 0) {
                LatLng actualCoordinates = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
                int dist = coordinates.distance(actualCoordinates);
                GmapsAddress address = new GmapsAddress(results[0].addressComponents);
                LOG.debug("Found address '{}' {} meters away with Google Maps", address.getInfo(", "), dist);
                return new ReverseResult<>(dist, ImmutableAddress.createFromAddress(address), actualCoordinates);
            }
        } catch (Exception ex) {
            LOG.info("Error getting address for '{}'", coordinates, ex);
        }

        return null;
    }

    protected abstract String buildQueryString(ImmutableAddress address);

    private static class GmapsAddress implements Address {

        private String street, city;
        private IAddressNumber number;
        private Integer postCode;

        public GmapsAddress(AddressComponent[] components) {
            for (AddressComponent component : components) {
                for (AddressComponentType type : component.types) {
                    switch (type) {
                        case ROUTE:
                            if (street == null) {
                                street = component.longName;
                            }
                            break;
                        case LOCALITY:
                            if (city == null) {
                                city = component.longName;
                            }
                            break;
                        case POSTAL_CODE:
                            if (postCode == null) {
                                try {
                                    this.postCode = Integer.parseInt(component.longName.trim());
                                } catch (NumberFormatException ignored) {
                                }
                            }
                            break;
                        case STREET_NUMBER:
                            if (number == null || number.getFrom() == null) {
                                number = new AddressNumber(component.longName);
                            }
                            break;
                    }
                }
            }
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
