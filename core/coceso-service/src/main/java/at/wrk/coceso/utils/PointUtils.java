package at.wrk.coceso.utils;

import at.wrk.fmd.mls.geocoding.api.dto.Address;
import at.wrk.fmd.mls.geocoding.api.dto.PointDto;

// TODO This probably should move to the Geocoding module?
// TODO How are we handling empty/blank strings?
public class PointUtils {

    public static boolean isEmpty(PointDto point) {
        if (point == null) {
            return true;
        }

        return point.getPoi() == null && isEmpty(point.getAddress()) && point.getDetails() == null && point.getCoordinates() == null;
    }

    public static boolean isEmpty(Address address) {
        if (address == null) {
            return true;
        }

        return address.getStreet() == null && address.getIntersection() == null
                && address.getNumber() == null && address.getBlock() == null && address.getDetails() == null
                && address.getPostCode() == null && address.getCity() == null;
    }

    public static PointDto toNullIfEmpty(PointDto point) {
        if (isEmpty(point)) {
            return null;
        }

        if (isEmpty(point.getAddress())) {
            return point.withAddress(null);
        }

        return point;
    }

    public static boolean equals(PointDto a, PointDto b) {
        // TODO
        return false;
    }

    public static String toString(PointDto point) {
        // TODO
        return "";
    }
}
