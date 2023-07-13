package at.wrk.geocode.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database repository for caching geocode results
 */
@Repository
interface CacheRepository extends JpaRepository<CacheEntry, Integer> {

  /**
   * Find all entries within the given LatLng bounds, ordered by distance to the given point
   *
   * @param lat Latitude of the center point in degrees
   * @param lng Longitude of the center point in degrees
   * @param latMin South boundary in degrees
   * @param latMax North boundary in degrees
   * @param lngMin West boundary in degrees
   * @param lngMax East boundary in degrees
   * @param pageable
   * @return
   */
  @Query("SELECT c"
      + " FROM CacheEntry c WHERE lat >= :latMin AND lat <= :latMax AND lng >= :lngMin AND lng <= :lngMax"
      + " ORDER BY acos(sin(radians(:lat)) * sin(radians(lat)) + cos(radians(:lat)) * cos(radians(lat)) * cos(radians(:lng - lng)))")
  List<CacheEntry> findNearest(@Param("lat") double lat, @Param("lng") double lng, @Param("latMin") double latMin,
      @Param("latMax") double latMax, @Param("lngMin") double lngMin, @Param("lngMax") double lngMax, Pageable pageable);

}
