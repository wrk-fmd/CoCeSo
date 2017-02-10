package at.wrk.geocode.poi;

import at.wrk.geocode.LatLng;

/**
 * Basic implementation for a POI
 */
class PoiImpl implements Poi {

  private String text;
  private LatLng coordinates;

  public PoiImpl() {
  }

  public PoiImpl(String text, LatLng coordinates) {
    this.text = text;
    this.coordinates = coordinates;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public LatLng getCoordinates() {
    return coordinates;
  }

}
