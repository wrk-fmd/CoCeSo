package at.wrk.geocode.impl;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.ImmutableAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Geocoder implementation for addresses using first/best result from all other address geocoder implementations
 */
@Component("ChainedGeocoder")
public class ChainedGeocoder implements Geocoder<ImmutableAddress> {

    private static final Logger LOG = LoggerFactory.getLogger(ChainedGeocoder.class);

    private static final ExampleMatcher addressMatcher = ExampleMatcher.matching()
            .withIgnoreCase()
            .withIncludeNullValues()
            .withIgnorePaths("id", "lat", "lng");

    @Autowired
    private CacheRepository cacheRepository;

    private final List<Geocoder<ImmutableAddress>> geocoders;

    public ChainedGeocoder() {
        geocoders = Collections.emptyList();
    }

    @Autowired(required = false)
    public ChainedGeocoder(List<Geocoder<ImmutableAddress>> geocoder) {
        this.geocoders = geocoder;
    }

    @Transactional(transactionManager = "geocodeTransactionManager")
    @Override
    public LatLng geocode(final ImmutableAddress address) {
        // First look in cache
        LatLng coordinates = getCoordinatesFromCache(address);

        if (coordinates == null) {
            // Now try all the geocoders in order
            LOG.trace("Lookup from cache did not return any geocode result. Fetch information from other geocoders for address: {}", address);
            coordinates = geocoders.stream()
                    .map(g -> g.geocode(address))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (coordinates != null) {
                LOG.trace("Found a geocoder result to store in the Geocode Cache: {} {}", address, coordinates);
                cacheRepository.save(new CacheEntry(address, coordinates));
            }
        }

        return coordinates;
    }

    @Transactional(transactionManager = "geocodeTransactionManager")
    @Override
    public ReverseResult<ImmutableAddress> reverse(LatLng coordinates) {
        boolean cache = false;

        // First look in cache
        ReverseResult<ImmutableAddress> nearest = findNearestFromCache(coordinates, 50);
        if (nearest != null && nearest.dist < 50) {
            LOG.debug("Found address {} meters from ({}) in cache", nearest.dist, coordinates);
            return nearest;
        }

        for (Geocoder<ImmutableAddress> geocoder : geocoders) {
            ReverseResult<ImmutableAddress> result = geocoder.reverse(coordinates);
            if (result != null) {
                if (nearest == null || result.dist < nearest.dist) {
                    nearest = result;
                    cache = true;
                }
                if (result.dist < 50) {
                    // Result is within 50 meters, so stop looking
                    break;
                }
            }
        }

        if (nearest != null && cache) {
            cacheRepository.save(new CacheEntry(nearest.result, nearest.coordinates));
        }

        return nearest;
    }

    private LatLng getCoordinatesFromCache(final Address address) {
        LatLng coordinates = null;

        LOG.trace("Search in cache for address '{}'.", address);
        List<CacheEntry> entries = cacheRepository.findAll(Example.of(new CacheEntry(address), addressMatcher));
        if (!entries.isEmpty()) {
            LOG.debug("Found coordinates for address '{}' in cache", address);
            coordinates = entries.get(0).getCoordinates();
        }

        return coordinates;
    }

    private ReverseResult<ImmutableAddress> findNearestFromCache(final LatLng coordinates, final int distance) {
        Bounds bounds = coordinates.boundsForDistance(distance);
        List<CacheEntry> entries = cacheRepository.findNearest(coordinates.getLat(), coordinates.getLng(),
                bounds.sw.getLat(), bounds.ne.getLat(), bounds.sw.getLng(), bounds.ne.getLng(), PageRequest.of(0, 1));
        if (entries.isEmpty()) {
            return null;
        }

        CacheEntry entry = entries.get(0);
        return new ReverseResult<>(coordinates.distance(entry.getCoordinates()), ImmutableAddress.createFromAddress(entry), entry.getCoordinates());
    }

}
