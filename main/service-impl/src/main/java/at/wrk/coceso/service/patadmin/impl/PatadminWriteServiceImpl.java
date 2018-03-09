package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.GroupsForm;
import at.wrk.coceso.service.patadmin.internal.PatadminServiceInternal;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.patadmin.PatadminWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PatadminWriteServiceImpl implements PatadminWriteService {

  @Autowired
  private PatadminServiceInternal patadminService;

  private final EntityEventFactory eef;
  private final EntityEventHandler<Unit> unitEventHandler;
  private final EntityEventListener<Unit> entityEventListener;

  @Autowired
  public PatadminWriteServiceImpl(EntityEventFactory eef) {
    this.eef = eef;
    unitEventHandler = eef.getEntityEventHandler(Unit.class);
    entityEventListener = eef.getWebSocketWriter("/topic/patadmin/groups/%d",
        JsonViews.Patadmin.class, u -> (u.getType() != null && u.getType().isTreatment() ? null : u.getId()));
    unitEventHandler.addListener(entityEventListener);
  }

  @PreDestroy
  public void destroy() {
    unitEventHandler.removeListener(entityEventListener);
  }

  @Override
  public void update(GroupsForm form, Concern concern, User user) {
    NotifyList.executeVoid(n -> patadminService.update(form.getGroups(), concern, user, n), eef);
  }

}
