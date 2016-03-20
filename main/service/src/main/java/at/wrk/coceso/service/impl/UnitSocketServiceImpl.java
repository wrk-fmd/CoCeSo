package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.service.ContainerSocketService;
import at.wrk.coceso.service.UnitService;
import java.util.List;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.UnitSocketService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UnitSocketServiceImpl implements UnitSocketService {

  @Autowired
  private UnitService unitService;

  @Autowired
  private ContainerSocketService containerSocketService;

  private final EntityEventHandler<Unit> entityEventHandler;
  private final EntityEventListener<Unit> mainEventListener;
  private final EntityEventListener<Unit> editEventListener;

  @Autowired
  public UnitSocketServiceImpl(SocketMessagingTemplate template) {
    entityEventHandler = EntityEventHandler.getInstance(Unit.class);
    mainEventListener = entityEventHandler.addListener(new WebSocketWriter<>(template, "/topic/unit/main/%d", JsonViews.Main.class, null));
    editEventListener = entityEventHandler.addListener(new WebSocketWriter<>(template, "/topic/unit/edit/%d", JsonViews.Edit.class, null));
  }

  @PreDestroy
  public void destroy() {
    entityEventHandler.removeListener(mainEventListener);
    entityEventHandler.removeListener(editEventListener);
  }

  @Override
  public Unit updateMain(Unit unit, User user) {
    return NotifyList.execute(n -> unitService.updateMain(unit, user, n));
  }

  @Override
  public Unit updateEdit(Unit unit, Concern concern, User user) {
    boolean isNew = unit.getId() == null;
    Unit u = NotifyList.execute(n -> unitService.updateEdit(unit, concern, user, n));
    if (isNew) {
      containerSocketService.notifyRoot(concern);
    }
    return u;
  }

  @Override
  public List<Integer> batchCreate(BatchUnits batch, Concern concern, User user) {
    List<Integer> created = NotifyList.execute(n -> unitService.batchCreate(batch, concern, user, n));
    containerSocketService.notifyRoot(concern);
    return created;
  }

  @Override
  public void remove(int unitId, User user) {
    Unit unit = unitService.doRemove(unitId, user);
    entityEventHandler.entityDeleted(unit.getId(), unit.getConcern().getId());
  }

  @Override
  public void sendHome(int unitId, User user) {
    NotifyList.executeVoid(n -> unitService.sendHome(unitId, user, n));
  }

  @Override
  public void holdPosition(int unitId, User user) {
    NotifyList.executeVoid(n -> unitService.holdPosition(unitId, user, n));
  }

  @Override
  public void standby(int unitId, User user) {
    NotifyList.executeVoid(n -> unitService.standby(unitId, user, n));
  }

  @Override
  public void removeCrew(int unit_id, int user_id) {
    NotifyList.executeVoid(n -> unitService.removeCrew(unit_id, user_id, n));
  }

  @Override
  public void addCrew(int unit_id, int user_id) {
    NotifyList.executeVoid(n -> unitService.addCrew(unit_id, user_id, n));
  }

  @Override
  public int importUnits(String data, Concern concern, User user) {
    int imported = NotifyList.execute(n -> unitService.importUnits(data, concern, user, n));
    containerSocketService.notifyRoot(concern);
    return imported;
  }

}
