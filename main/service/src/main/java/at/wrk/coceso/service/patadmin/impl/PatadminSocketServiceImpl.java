package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import at.wrk.coceso.form.GroupsForm;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.PatadminSocketService;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PatadminSocketServiceImpl implements PatadminSocketService {

  @Autowired
  private PatadminService patadminService;

  private final EntityEventHandler<Unit> unitEventHandler;
  private final EntityEventListener<Unit> entityEventListener;

  @Autowired
  public PatadminSocketServiceImpl(SocketMessagingTemplate template) {
    unitEventHandler = EntityEventHandler.getInstance(Unit.class);
    entityEventListener = unitEventHandler.addListener(new WebSocketWriter<>(template, "/topic/patadmin/groups/%d",
        JsonViews.Patadmin.class, u -> (u.getType() != null && u.getType().isTreatment() ? null : u.getId())));
  }

  @PreDestroy
  public void destroy() {
    unitEventHandler.removeListener(entityEventListener);
  }

  @Override
  public void update(GroupsForm form, Concern concern, User user) {
    NotifyList.executeVoid(n -> patadminService.update(form.getGroups(), concern, user, n));
  }

}
