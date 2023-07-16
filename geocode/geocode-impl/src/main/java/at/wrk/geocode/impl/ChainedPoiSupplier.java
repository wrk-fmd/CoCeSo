package at.wrk.geocode.impl;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.poi.Poi;
import at.wrk.geocode.poi.PoiSupplier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PoiSupplier implementation aggregating results from all other implementations
 */
@Component("ChainedPoi")
public class ChainedPoiSupplier implements PoiSupplier {

  private final List<PoiSupplier> poiSuppliers;

  public ChainedPoiSupplier() {
    poiSuppliers = Collections.emptyList();
  }

  @Autowired(required = false)
  public ChainedPoiSupplier(List<PoiSupplier> poiSuppliers) {
    this.poiSuppliers = poiSuppliers;
  }

  public ChainedPoiSupplier(PoiSupplier... poiSuppliers) {
    this.poiSuppliers = Arrays.asList(poiSuppliers);
  }

  @Override
  public Poi getPoi(String text) {
    return poiSuppliers.stream().map(s -> s.getPoi(text)).filter(Objects::nonNull).findFirst().orElse(null);
  }

  @Override
  public LatLng geocode(Poi search) {
    return poiSuppliers.stream().map(s -> s.geocode(search)).filter(Objects::nonNull).findFirst().orElse(null);
  }

  @Override
  public ReverseResult<Poi> reverse(LatLng coordinates) {
    return poiSuppliers.stream().map(s -> s.reverse(coordinates)).filter(Objects::nonNull).sorted().findFirst().orElse(null);
  }

}
