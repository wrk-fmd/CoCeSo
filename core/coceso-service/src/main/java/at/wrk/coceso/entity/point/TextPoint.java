package at.wrk.coceso.entity.point;

import at.wrk.fmd.mls.geocoding.api.dto.LatLng;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Objects;

/**
 * A point containing only an unresolved info text
 */
@Configurable
public class TextPoint implements Point {

    private static final Logger LOG = LoggerFactory.getLogger(TextPoint.class);

    private final String info;

    private TextPoint() {
        this.info = null;
    }

    private TextPoint(TextPoint p) {
        info = p.info;
    }

    public TextPoint(String info) {
        this.info = info;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public LatLng getCoordinates() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(info);
    }

    @Override
    public TextPoint deepCopy() {
        return new TextPoint(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextPoint)) {
            return false;
        }
        TextPoint textPoint = (TextPoint) o;
        return Objects.equals(info, textPoint.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }

    @Override
    public String toString() {
        return info == null ? "" : info;
    }
}
