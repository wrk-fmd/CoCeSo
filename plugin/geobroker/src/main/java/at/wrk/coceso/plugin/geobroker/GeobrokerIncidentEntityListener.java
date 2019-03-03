package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.external.ExternalIncidentIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerIncidentFactory;
import at.wrk.coceso.plugin.geobroker.loader.IncidentLoader;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GeobrokerIncidentEntityListener implements EntityEventListener<Incident> {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerIncidentEntityListener.class);

    private final GeoBrokerManager brokerManager;
    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final GeoBrokerIncidentFactory incidentFactory;
    private final IncidentLoader incidentLoader;

    @Autowired
    public GeobrokerIncidentEntityListener(
            final GeoBrokerManager brokerManager,
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final GeoBrokerIncidentFactory incidentFactory,
            final IncidentLoader incidentLoader) {
        this.brokerManager = brokerManager;
        this.incidentIdGenerator = incidentIdGenerator;
        this.incidentFactory = incidentFactory;
        this.incidentLoader = incidentLoader;
    }

    @Override
    public void entityChanged(final Incident entity, final int concern, final int hver, final int seq) {
        incidentUpdated(entity);
    }

    @Override
    public void entityDeleted(final int id, final int concern, final int hver, final int seq) {
        incidentDeleted(id, concern);
    }

    @Override
    public boolean isSupported(final Class<?> supportedClass) {
        return Incident.class.isAssignableFrom(supportedClass);
    }

    @EventListener
    public void onContextRefreshed(final ContextRefreshedEvent event) {
        LOG.info("Spring context was refreshed. Full update of all units is sent to geobroker.");
        Collection<Incident> initialIncidentState = incidentLoader.loadAllIncidentsOfActiveConcerns();
        LOG.debug("Found {} incidents in database to send to geobroker.", initialIncidentState.size());
        initialIncidentState.forEach(this::incidentUpdated);
    }

    private void incidentUpdated(final Incident entity) {
        executeSafely(() -> {
            CachedIncident incident = incidentFactory.createExternalIncident(entity);
            brokerManager.incidentUpdated(incident);
        });
    }

    private void incidentDeleted(final int id, final int concern) {
        executeSafely(() -> {
            String externalIncidentId = incidentIdGenerator.generateExternalIncidentId(id, concern);
            brokerManager.incidentDeleted(externalIncidentId);
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
