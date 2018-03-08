package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import at.wrk.geocode.LatLng;

import javax.annotation.Nullable;

public final class GeoBrokerPoints {
    private GeoBrokerPoints() {
    }

    public static GeoBrokerPoint mapPoint(@Nullable final Point position) {
        GeoBrokerPoint point = null;

        if (position != null) {
            LatLng coordinates = position.getCoordinates();
            point = new GeoBrokerPoint(coordinates.getLat(), coordinates.getLng());
        }

        return point;
    }
}
