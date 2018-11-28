package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.IAddressNumber;
import at.wrk.geocode.address.ImmutableAddress;
import at.wrk.geocode.util.AddressMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Order(40)
public class ViennaGeocoder implements Geocoder<ImmutableAddress> {

    private static final Logger LOG = LoggerFactory.getLogger(ViennaGeocoder.class);
    private static final String GEOCODE_URL = "https://data.wien.gv.at/daten/OGDAddressService.svc/GetAddressInfo?CRS=EPSG:4326&Address={query}";
    private static final String REVERSE_URL = "https://data.wien.gv.at/daten/OGDAddressService.svc/ReverseGeocode?CRS=EPSG:4326&type=A3:8012&location={lng},{lat}";
    private static final Bounds BOUNDS = new Bounds(new LatLng(48.1183, 16.1827), new LatLng(48.3231, 16.5787));

    private final AddressMatcher addressMatcher;
    private final RestTemplate restTemplate;

    @Autowired
    public ViennaGeocoder(final AddressMatcher addressMatcher, final RestTemplate restTemplate) {
        this.addressMatcher = addressMatcher;
        this.restTemplate = restTemplate;
    }

    @Override
    public LatLng geocode(final ImmutableAddress address) {
        if (address.getStreet() == null) {
            LOG.trace("Vienna Geocoder is not applicable if no street is set. Address: {}", address);
            return null;
        }

        if (address.getPostCode() != null && (address.getPostCode() < 1000 || address.getPostCode() > 1300)) {
            LOG.trace("Vienna Geocoder is not applicable if the postal code is outside of Vienna. Address: {}", address);
            return null;
        }

        if (address.getCity() != null && !address.getCity().toLowerCase().startsWith("wien")) {
            LOG.trace("Vienna Geocoder is not applicable if the city is not Vienna (=\"Wien\"). Address: {}", address);
            return null;
        }

        String query = buildQueryString(address);
        AddressInfoList infos;
        try {
            LOG.trace("Vienna Geocoder requests coordinates of address from Vienna OGDAddressService.");
            infos = restTemplate.getForObject(GEOCODE_URL, AddressInfoList.class, query);
        } catch (RestClientException e) {
            LOG.info("Failed to get geocode data from OGDAddressService for address: {}. Error: {}", address, e.getMessage());
            LOG.debug("Underlying exception:", e);
            return null;
        }

        if (infos == null || infos.count() <= 0) {
            LOG.trace("Got no usable information in the Vienna Geocoder response.");
            return null;
        }

        if (infos.count() > 1) {
            for (AddressInfoEntry entry : infos.getEntries()) {
                // First run: Look for exact match
                if (addressMatcher.isFoundAddressMatching(entry.getAddress(), address, true)) {
                    LOG.debug("Found an exactly matching address in the result set: {}.", entry);
                    return entry.getCoordinates();
                }
            }

            for (AddressInfoEntry entry : infos.getEntries()) {
                // Second run: Look for bigger addresses containing the requested
                if (addressMatcher.isFoundAddressMatching(entry.getAddress(), address, false)) {
                    LOG.debug("Found a matching address in the result set, with a different number: {}.", entry);
                    return entry.getCoordinates();
                }
            }
        }

        // Only one entry or no match found, use lowest ranking
        LOG.trace("Check if single entry in returned information is matching by levenshtein distance.");
        return addressMatcher.isStreetMatchingByLevenshtein(infos.getEntries()[0].getAddress(), address) ? infos.getEntries()[0].getCoordinates() : null;
    }

    @Override
    public ReverseResult<ImmutableAddress> reverse(LatLng coordinates) {
        if (!BOUNDS.contains(coordinates)) {
            return null;
        }

        try {
            LOG.trace("Perform reverser lookup for address on data.wien.gv.at for coordinates: {}", coordinates);
            AddressInfoList infos = restTemplate.getForObject(REVERSE_URL, AddressInfoList.class, coordinates.getLng(), coordinates.getLat());
            if (infos == null || infos.count() <= 0) {
                return null;
            }
            AddressInfoEntry entry = infos.getEntries()[0];
            int dist = coordinates.distance(entry.getCoordinates());
            LOG.debug("Found address '{}' {} meters away with data.wien.gv.at", entry.getAddress(), dist);
            return new ReverseResult<>(dist, ImmutableAddress.createFromAddress(entry.getAddress()), entry.getCoordinates());
        } catch (RestClientException ex) {
            LOG.info("Error getting address for '{}'", coordinates, ex);
        }

        return null;
    }

    private String buildQueryString(Address address) {
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
                if (number.getBlock() != null) {
                    query += "/" + number.getBlock();
                }
            }
        }

        return query;
    }

}
