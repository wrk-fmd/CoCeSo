package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.IncidentSocketService;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class IncidentSocketServiceImpl implements IncidentSocketService {

  @Autowired
  private IncidentService incidentService;

  private final EntityEventHandler<Incident> entityEventHandler;
  private final EntityEventListener<Incident> entityEventListener;

  @Autowired
  public IncidentSocketServiceImpl(SocketMessagingTemplate template) {
    entityEventHandler = EntityEventHandler.getInstance(Incident.class);
    entityEventListener = entityEventHandler.addListener(new WebSocketWriter<>(template, "/topic/incident/main/%d",
        JsonViews.Main.class, inc -> (inc.isRelevant() ? null : inc.getId())));
  }

  @PreDestroy
  public void destroy() {
    entityEventHandler.removeListener(entityEventListener);
  }

  @Override
  public Incident update(Incident incident, Concern concern, User user) {
    return NotifyList.execute(n -> incidentService.update(incident, concern, user, n));
  }

  @Override
  public void assignPatient(int incidentId, int patientId, User user) {
    NotifyList.executeVoid(n -> incidentService.assignPatient(incidentId, patientId, user, n));
  }

}
