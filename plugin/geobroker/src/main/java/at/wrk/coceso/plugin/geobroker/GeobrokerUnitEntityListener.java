package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerUnitFactory;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerUnitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeobrokerUnitEntityListener implements EntityEventListener<Unit> {

    private final GeoBrokerUnitListener unitListener;
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final GeoBrokerUnitFactory unitFactory;

    @Autowired
    public GeobrokerUnitEntityListener(
            final GeoBrokerUnitListener unitListener,
            final ExternalUnitIdGenerator unitIdGenerator,
            final GeoBrokerUnitFactory unitFactory) {
        this.unitListener = unitListener;
        this.unitIdGenerator = unitIdGenerator;
        this.unitFactory = unitFactory;
    }

    @Override
    public void entityChanged(final Unit entity, final int concern, final int hver, final int seq) {
        GeoBrokerUnit externalUnit = unitFactory.createExternalUnit(entity);
        unitListener.unitUpdated(externalUnit);
    }

    @Override
    public void entityDeleted(final int id, final int concern, final int hver, final int seq) {
        String externalUnitId = unitIdGenerator.generateExternalUnitId(id, concern);
        unitListener.unitDeleted(externalUnitId);
    }

    @Override
    public boolean isSupported(final Class<?> supportedClass) {
        return Unit.class.isAssignableFrom(supportedClass);
    }
}
