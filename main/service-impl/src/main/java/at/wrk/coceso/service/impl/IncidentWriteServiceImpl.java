package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.IncidentWriteService;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.function.Function;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class IncidentWriteServiceImpl implements IncidentWriteService {

    private static final Function<Incident, Integer> INCIDENT_DELETE_FUNCTION = incident -> (incident.isRelevant() ? null : incident.getId());

    private final IncidentServiceInternal incidentService;
    private final EntityEventFactory entityEventFactory;
    private final EntityEventHandler<Incident> entityEventHandler;
    private final EntityEventListener<Incident> entityEventListener;

    @Autowired
    public IncidentWriteServiceImpl(
            final EntityEventFactory entityEventFactory,
            final IncidentServiceInternal incidentService) {
        this.entityEventFactory = entityEventFactory;
        this.incidentService = incidentService;

        this.entityEventHandler = entityEventFactory.getEntityEventHandler(Incident.class);
        this.entityEventListener = entityEventFactory.getWebSocketWriter("/topic/incident/main/%d", JsonViews.Main.class, INCIDENT_DELETE_FUNCTION);

        this.entityEventHandler.addListener(entityEventListener);
    }

    @PreDestroy
    public void destroy() {
        entityEventHandler.removeListener(entityEventListener);
    }

    @Override
    public Incident update(final Incident incident, final Concern concern) {
        return NotifyList.execute(notifyList -> incidentService.update(incident, concern, notifyList), entityEventFactory);
    }

    @Override
    public void assignPatient(final int incidentId, final int patientId) {
        NotifyList.executeVoid(notifyList -> incidentService.assignPatient(incidentId, patientId, notifyList), entityEventFactory);
    }
}
