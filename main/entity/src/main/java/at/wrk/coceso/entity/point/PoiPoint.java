package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.Poi;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A Point representing a POI
 */
@Configurable
class PoiPoint implements Poi, Point {

  // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
  @Autowired
  @Qualifier("ChainedGeocoder")
  private Geocoder<Address> addressGeocoder;

  @Autowired
  @Qualifier("ChainedPoi")
  private Geocoder<Poi> poiGeocoder;

  private final String text, additional;
  private LatLng coordinates;

  private PoiPoint() {
    this.text = null;
    this.additional = null;
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
    fill();
    return coordinates;
  }

  private void fill() {
    if (coordinates == null && !isEmpty()) {
      coordinates = poiGeocoder.geocode(this);
      if (coordinates == null) {
        // No coordinates in POI entry, use normal address
        coordinates = addressGeocoder.geocode(new AddressPoint(getInfo()));
      }
    }
  }

  @Override
  public boolean isEmpty() {
    return StringUtils.isEmpty(text);
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
