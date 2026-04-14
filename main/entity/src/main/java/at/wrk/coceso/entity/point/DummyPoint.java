package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.LatLng;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A dummy for unresolved point data that should not actually be stored anywhere, but resolved by using #create()
 */
public class DummyPoint implements Point {
  private static final Logger LOG = LoggerFactory.getLogger(DummyPoint.class);

  private final String info;

  private DummyPoint() {
    this.info = null;
  }

  private DummyPoint(DummyPoint p) {
    info = p.info;
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
  public DummyPoint deepCopy() {
    return new DummyPoint(this);
  }

  @Override
  public Point create(Concern concern) {
    return isEmpty() ? null : Point.create(info, concern, true);
  }

  @Override
  public void tryToResolveExternalData() {
    // This should not happen, because a dummy point is always resolved in service code.
    LOG.warn("Trying to resolve a dummy point! Please check workflow on data validation.");
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
