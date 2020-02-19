package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.ImmutableAddress;
import at.wrk.geocode.poi.Poi;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Objects;

/**
 * A Point representing geographic coordinates
 */
@Configurable
public class CoordinatePoint implements Point {
  private static final Logger LOG = LoggerFactory.getLogger(CoordinatePoint.class);

  // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
  @Autowired
  @Qualifier("ChainedGeocoder")
  private Geocoder<ImmutableAddress> addressGeocoder;

  @Autowired
  @Qualifier("ChainedPoi")
  private Geocoder<Poi> poiGeocoder;

  private boolean filled = false;

  private String info;
  private final LatLng coordinates;

  private CoordinatePoint() {
    this.coordinates = null;
  }

  private CoordinatePoint(CoordinatePoint p) {
    info = p.info;
    coordinates = p.coordinates;
  }

  public CoordinatePoint(LatLng coordinates) {
    this.coordinates = coordinates;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointMinimal.class})
  @Override
  public String getInfo() {
    return info;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointMinimal.class})
  @Override
  public LatLng getCoordinates() {
    return coordinates;
  }

  @Override
  public void tryToResolveExternalData() {
    if (!filled && StringUtils.isEmpty(info) && !isEmpty()) {
      filled = true;
      LOG.debug("Coordinate point was not resolved yet. POI and Adress geocoders are called.");

      ReverseResult<Poi> poi = poiGeocoder.reverse(coordinates);
      if (poi != null && poi.dist <= 20) {
        info = poi.result.getText();
        return;
      }

      ReverseResult<ImmutableAddress> address = addressGeocoder.reverse(coordinates);
      if (poi != null) {
        info = address == null || address.dist >= poi.dist ? poi.result.getText() : address.result.getInfo();
      } else if (address != null) {
        info = address.result.getInfo();
      }
    }
  }

  @Override
  public boolean isEmpty() {
    return coordinates == null;
  }

  @Override
  public CoordinatePoint deepCopy() {
    return new CoordinatePoint(this);
  }

  @Override
  public String toString() {
    return coordinates == null ? "Empty CoordinatePoint" : coordinates.toString();
  }

  @Override
  public int hashCode() {
    return 37 + Objects.hashCode(this.coordinates);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CoordinatePoint other = (CoordinatePoint) obj;
    return Objects.equals(this.coordinates, other.coordinates);
  }

}
