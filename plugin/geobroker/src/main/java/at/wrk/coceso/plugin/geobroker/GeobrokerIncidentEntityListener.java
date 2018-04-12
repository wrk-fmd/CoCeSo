package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.external.ExternalIncidentIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerIncidentFactory;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GeobrokerIncidentEntityListener implements EntityEventListener<Incident> {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerIncidentEntityListener.class);

    private final GeoBrokerManager brokerManager;
    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final GeoBrokerIncidentFactory incidentFactory;

    @Autowired
    public GeobrokerIncidentEntityListener(
            final GeoBrokerManager brokerManager,
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final GeoBrokerIncidentFactory incidentFactory) {
        this.brokerManager = brokerManager;
        this.incidentIdGenerator = incidentIdGenerator;
        this.incidentFactory = incidentFactory;
    }

    @Override
    public void entityChanged(final Incident entity, final int concern, final int hver, final int seq) {
        executeSafely(() -> {
            CachedIncident incident = incidentFactory.createExternalIncident(entity);
            brokerManager.incidentUpdated(incident);
        });
    }

    @Override
    public void entityDeleted(final int id, final int concern, final int hver, final int seq) {
        executeSafely(() -> {
            String externalIncidentId = incidentIdGenerator.generateExternalIncidentId(id, concern);
            brokerManager.incidentDeleted(externalIncidentId);
        });
    }

    @Override
    public boolean isSupported(final Class<?> supportedClass) {
        return Incident.class.isAssignableFrom(supportedClass);
    }

    private void executeSafely(final Runnable execution) {
        try {
            execution.run();
        } catch (Throwable t) {
            LOG.error("Uncaught exception in GeoBroker plugin.", t);
        }
    }
}
