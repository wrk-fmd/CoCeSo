package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.geocode.LatLng;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class UnitPoint implements Point {

  @Autowired
  private UnitSupplier unitSupplier;

  private boolean filled = false;

  private final int id;
  private final String additional;
  private String call;
  private LatLng coordinates;

  private UnitPoint() {
    this.id = 0;
    this.additional = null;
  }

  public UnitPoint(int unitId) {
    this.id = unitId;
    this.additional = null;
  }

  public UnitPoint(Unit unit, String additional) {
    this.id = unit.getId();
    this.call = unit.getCall();
    this.coordinates = unit.getPosition() == null ? null : unit.getPosition().getCoordinates();
    this.additional = additional;
  }

  @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
  public int getId() {
    return id;
  }

  @JsonView(JsonViews.PointMinimal.class)
  @Override
  public String getInfo() {
    fill();
    return StringUtils.isBlank(additional) ? call : call + "\n" + additional;
  }

  @JsonView(JsonViews.PointMinimal.class)
  @Override
  public LatLng getCoordinates() {
    fill();
    return coordinates;
  }

  private void fill() {
    if (!filled && (call == null || coordinates == null) && !isEmpty()) {
      // Never try to fill more than once
      filled = true;

      Unit unit = unitSupplier.getById(id);
      if (unit != null && unit.getType().isTreatment()) {
        call = unit.getCall();

        // The position of the referred unit should never be another UnitPoint, as this might lead to endless recursion
        Point position = unit.getPosition();
        coordinates = (position == null || position instanceof UnitPoint) ? null : position.getCoordinates();
      }
    }
  }

  @Override
  public boolean isEmpty() {
    return id <= 0;
  }

  @Override
  public String toString() {
    String info = getInfo();
    return info == null ? "" : info;
  }

  @Override
  public int hashCode() {
    return 15 * id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final UnitPoint other = (UnitPoint) obj;
    return id == other.id;
  }

}
