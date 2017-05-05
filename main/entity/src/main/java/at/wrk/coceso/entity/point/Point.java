package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.Poi;
import at.wrk.geocode.poi.PoiSupplier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * General interface for a point
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = DummyPoint.class)
@JsonSubTypes({
  @Type(value = AddressPoint.class, name = "address"),
  @Type(value = CoordinatePoint.class, name = "coordinate"),
  @Type(value = DummyPoint.class, name = "dummy"),
  @Type(value = PoiPoint.class, name = "poi"),
  @Type(value = UnitPoint.class, name = "unit")
})
public interface Point extends Serializable {

  static final Pattern coordinatePattern = Pattern.compile("^\\(?(\\s*\\d+(\\.\\d+)?)(,| +)(\\d+(\\.\\d+)?)\\)?$", Pattern.UNICODE_CHARACTER_CLASS);

  /**
   * A human readable String representation of the Point
   *
   * @return
   */
  String getInfo();

  /**
   * The geographic coordinates of the Point
   *
   * @return
   */
  LatLng getCoordinates();

  /**
   * True if the instance represents an empty point
   *
   * @return
   */
  @JsonIgnore
  boolean isEmpty();

  /**
   * Create a copy of the point
   *
   * @return
   */
  Point deepCopy();

  /**
   * Resolve the information stored in the instance to the appropriate subclass
   *
   * @param concern The concern, for which the resolving should take place
   * @return A Point implementation if the point is not empty, null otherwise
   */
  default Point create(Concern concern) {
    return isEmpty() ? null : this;
  }

  /**
   * Wrapper for calling #create(Concern) with a null check
   *
   * @param point
   * @param concern
   * @return Null if point is null, result of calling #create(concern) on point otherwise
   */
  public static Point create(Point point, Concern concern) {
    return point == null ? null : point.create(concern);
  }

  /**
   * Create an instance of the appropriate subclass for the given info
   *
   * @param info
   * @param concern
   * @param poiSupplier
   * @param unitSupplier
   * @return Null if info is blank, a Point instance otherwise
   */
  public static Point create(String info, Concern concern, PoiSupplier poiSupplier, UnitSupplier unitSupplier) {
    info = StringUtils.trimToNull(info);

    if (info == null) {
      return null;
    }

    String[] parts = info.split("\n", 2);
    Matcher matchedCoordinates = coordinatePattern.matcher(parts[0].trim());
    if (matchedCoordinates.find(0)) {
      try {
        double lat = Double.parseDouble(matchedCoordinates.group(1)),
            lng = Double.parseDouble(matchedCoordinates.group(4));
        return new CoordinatePoint(new LatLng(lat, lng));
      } catch (NumberFormatException e) {

      }
    }

    if (unitSupplier != null && concern != null) {
      String call = parts[0];
      Unit group = unitSupplier.getTreatmentByCall(call, concern);
      if (group != null) {
        return new UnitPoint(group, parts.length >= 2 ? StringUtils.trimToNull(parts[1]) : null);
      }
    }

    Poi poi = poiSupplier.getPoi(AutocompleteSupplier.getKey(info));
    return poi == null ? new AddressPoint(info) : new PoiPoint(poi, info);
  }

  /**
   * Wrapper for calling #isEmpty() with a null check
   *
   * @param point
   * @return True if point is null, result of calling #isEmpty() on point otherwise
   */
  public static boolean isEmpty(Point point) {
    return point == null || point.isEmpty();
  }

  /**
   * Wrapper for calling #toString() with a null check
   *
   * @param point
   * @return Null if point is null or empty, result of calling #toString() on point otherwise
   */
  public static String toStringOrNull(Point point) {
    return isEmpty(point) ? null : point.toString();
  }

  /**
   * Compare to points based on their info string
   *
   * @param a
   * @param b
   * @return True if points are both null or info strings are equal, false otherwise
   */
  public static boolean infoEquals(Point a, Point b) {
    return Objects.equals(
        a == null ? null : StringUtils.defaultIfEmpty(a.getInfo(), null),
        b == null ? null : StringUtils.defaultIfEmpty(b.getInfo(), null)
    );
  }

}
