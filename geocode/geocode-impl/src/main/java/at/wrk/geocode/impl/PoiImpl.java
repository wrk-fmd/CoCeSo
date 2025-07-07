package at.wrk.geocode.impl;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.Poi;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Basic implementation for a POI
 */
class PoiImpl implements Poi {

  private final String text;
  private final LatLng coordinates;

  public PoiImpl() {
    this.text = null;
    this.coordinates = null;
  }

  public PoiImpl(final String text, final LatLng coordinates) {
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

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .append("text", text)
            .append("coordinates", coordinates)
            .toString();
  }
}
