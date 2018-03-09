package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.IncidentWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class IncidentWriteServiceImpl implements IncidentWriteService {

  @Autowired
  private IncidentServiceInternal incidentService;

  private final EntityEventFactory eef;
  private final EntityEventHandler<Incident> entityEventHandler;
  private final EntityEventListener<Incident> entityEventListener;

  @Autowired
  public IncidentWriteServiceImpl(EntityEventFactory eef) {
    this.eef = eef;
    entityEventHandler = eef.getEntityEventHandler(Incident.class);
    entityEventListener = eef.getWebSocketWriter("/topic/incident/main/%d",
        JsonViews.Main.class, inc -> (inc.isRelevant() ? null : inc.getId()));
    entityEventHandler.addListener(entityEventListener);
  }

  @PreDestroy
  public void destroy() {
    entityEventHandler.removeListener(entityEventListener);
  }

  @Override
  public Incident update(Incident incident, Concern concern, User user) {
    return NotifyList.execute(n -> incidentService.update(incident, concern, user, n), eef);
  }

  @Override
  public void assignPatient(int incidentId, int patientId, User user) {
    NotifyList.executeVoid(n -> incidentService.assignPatient(incidentId, patientId, user, n), eef);
  }

}
