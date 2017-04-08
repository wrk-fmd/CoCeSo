package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.PoiSupplier;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A dummy for unresolved point data that should not actually be stored anywhere, but resolved by using #create()
 */
@Configurable
public class DummyPoint implements Point {

  // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
  @Autowired
  @Qualifier("ChainedPoi")
  private PoiSupplier poiSupplier;

  @Autowired
  private UnitSupplier unitSupplier;

  private final String info;

  private DummyPoint() {
    this.info = null;
  }

  public DummyPoint(String info) {
    this.info = info;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointMinimal.class})
  @Override
  public String getInfo() {
    return info;
  }

  @JsonView(JsonViews.PointMinimal.class)
  @Override
  public LatLng getCoordinates() {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return StringUtils.isBlank(info);
  }

  @Override
  public Point create(Concern concern) {
    return isEmpty() ? null : Point.create(info, concern, poiSupplier, unitSupplier);
  }

  @Override
  public String toString() {
    return info == null ? "" : info;
  }

  @Override
  public int hashCode() {
    return 15 + Objects.hashCode(this.info);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DummyPoint other = (DummyPoint) obj;
    return Objects.equals(this.info, other.info);
  }

}
