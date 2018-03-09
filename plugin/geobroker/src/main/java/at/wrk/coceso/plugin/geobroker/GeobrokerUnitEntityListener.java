package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerUnitFactory;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeobrokerUnitEntityListener implements EntityEventListener<Unit> {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerUnitEntityListener.class);

    private final GeoBrokerManager brokerManager;
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final GeoBrokerUnitFactory unitFactory;

    @Autowired
    public GeobrokerUnitEntityListener(
            final GeoBrokerManager brokerManager,
            final ExternalUnitIdGenerator unitIdGenerator,
            final GeoBrokerUnitFactory unitFactory) {
        this.brokerManager = brokerManager;
        this.unitIdGenerator = unitIdGenerator;
        this.unitFactory = unitFactory;
    }

    @Override
    public void entityChanged(final Unit entity, final int concern, final int hver, final int seq) {
        executeSafely(() -> {
            CachedUnit externalUnit = unitFactory.createExternalUnit(entity);
            brokerManager.unitUpdated(externalUnit);
        });
    }

    @Override
    public void entityDeleted(final int id, final int concern, final int hver, final int seq) {
        executeSafely(() -> {
            String externalUnitId = unitIdGenerator.generateExternalUnitId(id, concern);
            brokerManager.unitDeleted(externalUnitId);
        });
    }

    @Override
    public boolean isSupported(final Class<?> supportedClass) {
        return Unit.class.isAssignableFrom(supportedClass);
    }

    private void executeSafely(final Runnable execution) {
        try {
            execution.run();
        } catch (Throwable t) {
            LOG.error("Uncaught exception in GeoBroker plugin.", t);
        }
    }
}
