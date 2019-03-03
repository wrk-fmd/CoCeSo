package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerUnitFactory;
import at.wrk.coceso.plugin.geobroker.loader.UnitLoader;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GeobrokerUnitEntityListener implements EntityEventListener<Unit> {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerUnitEntityListener.class);

    private final GeoBrokerManager brokerManager;
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final GeoBrokerUnitFactory unitFactory;
    private final UnitLoader unitLoader;

    @Autowired
    public GeobrokerUnitEntityListener(
            final GeoBrokerManager brokerManager,
            final ExternalUnitIdGenerator unitIdGenerator,
            final GeoBrokerUnitFactory unitFactory,
            final UnitLoader unitLoader) {
        this.brokerManager = brokerManager;
        this.unitIdGenerator = unitIdGenerator;
        this.unitFactory = unitFactory;
        this.unitLoader = unitLoader;
    }

    @Override
    public void entityChanged(final Unit entity, final int concern, final int hver, final int seq) {
        unitUpdated(entity);
    }

    @Override
    public void entityDeleted(final int id, final int concern, final int hver, final int seq) {
        unitDeleted(id, concern);
    }

    @Override
    public boolean isSupported(final Class<?> supportedClass) {
        return Unit.class.isAssignableFrom(supportedClass);
    }

    @EventListener
    public void onContextRefreshed(final ContextRefreshedEvent event) {
        LOG.info("Spring context was refreshed. Full update of all units is sent to geobroker.");
        Collection<Unit> initialUnitState = unitLoader.loadAllUnitsOfActiveConcerns();
        LOG.debug("Found {} units in database to send to geobroker.", initialUnitState.size());
        initialUnitState.forEach(this::unitUpdated);
    }

    private void unitUpdated(final Unit entity) {
        executeSafely(() -> {
            CachedUnit externalUnit = unitFactory.createExternalUnit(entity);
            brokerManager.unitUpdated(externalUnit);
        });
    }

    private void unitDeleted(final int id, final int concern) {
        executeSafely(() -> {
            String externalUnitId = unitIdGenerator.generateExternalUnitId(id, concern);
            brokerManager.unitDeleted(externalUnitId);
        });
    }

    private void executeSafely(final Runnable execution) {
        try {
            execution.run();
        } catch (Throwable t) {
            LOG.error("Uncaught exception in GeoBroker plugin.", t);
        }
    }
}
