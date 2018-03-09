package at.wrk.geocode.poi;

import at.wrk.geocode.Geocoder;

/**
 * Interface for a class capable of resolving textual descriptions to a Poi instance
 */
public interface PoiSupplier extends Geocoder<Poi> {

  /**
   *
   * @param text
   * @return
   */
  Poi getPoi(String text);
}
