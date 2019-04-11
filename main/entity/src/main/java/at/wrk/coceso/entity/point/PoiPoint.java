package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
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
 * A Point representing a POI
 */
@Configurable
class PoiPoint implements Poi, Point {
  private static final Logger LOG = LoggerFactory.getLogger(PoiPoint.class);

  // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
  @Autowired
  @Qualifier("ChainedGeocoder")
  private Geocoder<ImmutableAddress> addressGeocoder;

  @Autowired
  @Qualifier("ChainedPoi")
  private Geocoder<Poi> poiGeocoder;

  private boolean filled = false;

  private final String text, additional;
  private LatLng coordinates;

  private PoiPoint() {
    this.text = null;
    this.additional = null;
  }

  private PoiPoint(PoiPoint p) {
    text = p.text;
    additional = p.additional;
    coordinates = p.coordinates;
  }

  PoiPoint(String text, String additional) {
    this.text = text;
    this.additional = additional;
  }

  public PoiPoint(Poi poi, String info) {
    this.text = poi.getText();
    this.additional = info == null ? null : StringUtils.trimToNull(info.substring(poi.getText().length()));
    this.coordinates = poi.getCoordinates();
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  @Override
  public String getText() {
    return text;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  public String getAdditional() {
    return additional;
  }

  @JsonView(JsonViews.PointMinimal.class)
  @Override
  public String getInfo() {
    String info = "";
    if (text != null) {
      info += text;
    }
    if (additional != null) {
      info += additional;
    }
    return info;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointMinimal.class})
  @Override
  public LatLng getCoordinates() {
    return coordinates;
  }

  @Override
  public void tryToResolveExternalData() {
    if (!filled && coordinates == null && !isEmpty()) {
      filled = true;
      LOG.debug("POI point is not yet resolved. POI geocoder is called.");

      coordinates = poiGeocoder.geocode(this);
      if (coordinates == null) {
        // No coordinates in POI entry, use normal address
        ImmutableAddress immutableAddress = ImmutableAddress.createFromAddress(AddressPointParser.parseFromString(getInfo()));
        coordinates = addressGeocoder.geocode(immutableAddress);
      }
    }
  }

  @Override
  public boolean isEmpty() {
    return StringUtils.isEmpty(text);
  }

  @Override
  public PoiPoint deepCopy() {
    return new PoiPoint(this);
  }

  @Override
  public String toString() {
    return getInfo();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.text);
    hash = 29 * hash + Objects.hashCode(this.additional);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final PoiPoint other = (PoiPoint) obj;
    return Objects.equals(this.text, other.text)
        && Objects.equals(this.additional, other.additional);
  }

}
