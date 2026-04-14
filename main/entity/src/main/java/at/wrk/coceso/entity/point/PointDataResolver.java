package at.wrk.coceso.entity.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.geocode.Geocoder;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.ImmutableAddress;
import at.wrk.geocode.poi.Poi;
import at.wrk.geocode.poi.PoiSupplier;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PointDataResolver {

    private static PointDataResolver instance;

    private final Geocoder<ImmutableAddress> addressGeocoder;
    private final Geocoder<Poi> poiGeocoder;
    private final PoiSupplier poiSupplier;
    private final UnitSupplier unitSupplier;

    public PointDataResolver(
        @Qualifier("ChainedGeocoder") Geocoder<ImmutableAddress> addressGeocoder,
        @Qualifier("ChainedPoi") Geocoder<Poi> poiGeocoder,
        @Qualifier("ChainedPoi") PoiSupplier poiSupplier,
        UnitSupplier unitSupplier
    ) {
        this.addressGeocoder = addressGeocoder;
        this.poiGeocoder = poiGeocoder;
        this.poiSupplier = poiSupplier;
        this.unitSupplier = unitSupplier;
    }

    @PostConstruct
    void setInstance() {
        instance = this;
    }

    public static LatLng geocodeAddress(ImmutableAddress address) {
        return instance.addressGeocoder.geocode(address);
    }

    public static ReverseResult<ImmutableAddress> reverseToAddress(LatLng coordinates) {
        return instance.addressGeocoder.reverse(coordinates);
    }

    public static LatLng geocodePoi(Poi poi) {
        return instance.poiGeocoder.geocode(poi);
    }

    public static ReverseResult<Poi> reverseToPoi(LatLng coordinates) {
        return instance.poiGeocoder.reverse(coordinates);
    }

    public static Poi getPoi(String text) {
        return instance.poiSupplier.getPoi(text);
    }

    public static Unit getUnitById(int id) {
        return instance.unitSupplier.getById(id);
    }

    public static Unit getTreatmentByCall(String call, Concern concern) {
        return instance.unitSupplier.getTreatmentByCall(call, concern);
    }
}
