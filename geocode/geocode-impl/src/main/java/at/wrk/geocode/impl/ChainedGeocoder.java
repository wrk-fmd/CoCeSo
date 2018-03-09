package at.wrk.geocode.impl;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Geocoder implementation for addresses using first/best result from all other address geocoder implementations
 */
@Component("ChainedGeocoder")
public class ChainedGeocoder implements Geocoder<Address> {

  private static final Logger LOG = LoggerFactory.getLogger(ChainedGeocoder.class);

  private static final ExampleMatcher addressMatcher = ExampleMatcher.matching()
      .withIgnoreCase()
      .withIncludeNullValues()
      .withIgnorePaths("id", "lat", "lng");

  @Autowired
  private CacheRepository cacheRepository;

  private final List<Geocoder<Address>> geocoders;

  public ChainedGeocoder() {
    geocoders = Collections.emptyList();
  }

  @Autowired(required = false)
  public ChainedGeocoder(List<Geocoder<Address>> geocoder) {
    this.geocoders = geocoder;
  }

  public ChainedGeocoder(Geocoder<Address>... geocoders) {
    this.geocoders = Arrays.asList(geocoders);
  }

  @Transactional(transactionManager = "geocodeTransactionManager")
  @Override
  public LatLng geocode(Address address) {
    // First look in cache
    List<CacheEntry> entries = cacheRepository.findAll(Example.of(new CacheEntry(address), addressMatcher));
    if (!entries.isEmpty()) {
      LOG.debug("Found coordinates for address '{}' in cache", address);
      return entries.get(0).getCoordinates();
    }

    // Now try all the geocoders in order
    LatLng coordinates = geocoders.stream().map(g -> g.geocode(address)).filter(Objects::nonNull).findFirst().orElse(null);
    if (coordinates != null) {
      cacheRepository.save(new CacheEntry(address, coordinates));
    }
    return coordinates;
  }

  @Transactional(transactionManager = "geocodeTransactionManager")
  @Override
  public ReverseResult<Address> reverse(LatLng coordinates) {
    boolean cache = false;

    // First look in cache
    ReverseResult<Address> nearest = findNearestFromCache(coordinates, 50);
    if (nearest != null && nearest.dist < 50) {
      LOG.debug("Found address {} meters from ({}) in cache", nearest.dist, coordinates);
      return nearest;
    }

    for (Geocoder<Address> geocoder : geocoders) {
      ReverseResult<Address> result = geocoder.reverse(coordinates);
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

  private ReverseResult<Address> findNearestFromCache(LatLng coordinates, int distance) {
    Bounds bounds = coordinates.boundsForDistance(distance);
    List<CacheEntry> entries = cacheRepository.findNearest(coordinates.getLat(), coordinates.getLng(),
        bounds.sw.getLat(), bounds.ne.getLat(), bounds.sw.getLng(), bounds.ne.getLng(), new PageRequest(0, 1));
    if (entries.isEmpty()) {
      return null;
    }

    CacheEntry entry = entries.get(0);
    return new ReverseResult<>(coordinates.distance(entry.getCoordinates()), entry, entry.getCoordinates());
  }

}
