package at.wrk.geocode.poi;

import at.wrk.geocode.LatLng;

/**
 * Interface for objects representing a Point of Interest
 */
public interface Poi {

  String getText();

  LatLng getCoordinates();

}
