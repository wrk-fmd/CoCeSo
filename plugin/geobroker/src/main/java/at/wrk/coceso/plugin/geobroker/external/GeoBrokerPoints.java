package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerPoint;
import at.wrk.geocode.LatLng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

final class GeoBrokerPoints {
    private static final Logger LOG = LoggerFactory.getLogger(GeoBrokerPoints.class);

    private GeoBrokerPoints() {
    }

    static GeoBrokerPoint mapPoint(@Nullable final Point position) {
        GeoBrokerPoint point = null;

        if (position != null && position.getCoordinates() != null) {
            LatLng coordinates = position.getCoordinates();
            point = new GeoBrokerPoint(coordinates.getLat(), coordinates.getLng());
        }

        return point;
    }
}
