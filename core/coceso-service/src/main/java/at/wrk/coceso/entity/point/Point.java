package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.fmd.mls.geocoding.api.dto.LatLng;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * General interface for a point
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = TextPoint.class)
@JsonSubTypes({
//        @Type(value = AddressPoint.class, name = "address"),
//        @Type(value = CoordinatePoint.class, name = "coordinate"),
        @Type(value = TextPoint.class, name = "dummy"),
//        @Type(value = PoiPoint.class, name = "poi"),
//        @Type(value = UnitPoint.class, name = "unit")
})
public interface Point extends Serializable {

    Logger LOG = LoggerFactory.getLogger(Point.class);

    Pattern COORDINATE_PATTERN = Pattern.compile("^\\(?(\\s*\\d+(\\.\\d+)?)(,| +)(\\d+(\\.\\d+)?)\\)?$", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * A human readable String representation of the Point.
     */
    String getInfo();

    /**
     * The geographic coordinates of the Point.
     */
    LatLng getCoordinates();

    /**
     * True if the instance represents an empty point.
     */
    @JsonIgnore
    boolean isEmpty();

    /**
     * Create a copy of the point.
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
     * Wrapper for calling #create(Concern) with a null check.
     *
     * @return Null if point is null, result of calling #create(concern) on point otherwise
     */
    static Point create(Point point, Concern concern) {
        return point == null ? null : point.create(concern);
    }

    /**
     * Wrapper for calling #isEmpty() with a null check
     *
     * @return True if point is null, result of calling #isEmpty() on point otherwise
     */
    static boolean isEmpty(Point point) {
        return point == null || point.isEmpty();
    }

    /**
     * Wrapper for calling #toString() with a null check
     *
     * @param point
     * @return Null if point is null or empty, result of calling #toString() on point otherwise
     */
    static String toStringOrNull(Point point) {
        return isEmpty(point) ? null : point.toString();
    }

    /**
     * Compare to points based on their info string
     *
     * @param a
     * @param b
     * @return True if points are both null or info strings are equal, false otherwise
     */
    static boolean infoEquals(Point a, Point b) {
        return Objects.equals(
                a == null ? null : StringUtils.defaultIfEmpty(a.getInfo(), null),
                b == null ? null : StringUtils.defaultIfEmpty(b.getInfo(), null)
        );
    }
}
