package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.UnitServiceInternal;
import java.util.List;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.ContainerWriteService;
import at.wrk.coceso.service.UnitWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UnitWriteServiceImpl implements UnitWriteService {

  @Autowired
  private UnitServiceInternal unitService;

  @Autowired
  private ContainerWriteService containerWriteService;

  private final EntityEventFactory eef;
  private final EntityEventHandler<Unit> entityEventHandler;
  private final EntityEventListener<Unit> mainEventListener;
  private final EntityEventListener<Unit> editEventListener;

  @Autowired
  public UnitWriteServiceImpl(EntityEventFactory eef) {
    this.eef = eef;
    entityEventHandler = eef.getEntityEventHandler(Unit.class);

    mainEventListener = eef.getWebSocketWriter("/topic/unit/main/%d", JsonViews.Main.class, null);
    editEventListener = eef.getWebSocketWriter("/topic/unit/edit/%d", JsonViews.Edit.class, null);
    entityEventHandler.addListener(mainEventListener);
    entityEventHandler.addListener(editEventListener);
  }

  @PreDestroy
  public void destroy() {
    entityEventHandler.removeListener(mainEventListener);
    entityEventHandler.removeListener(editEventListener);
  }

  @Override
  public Unit updateMain(Unit unit, User user) {
    return NotifyList.execute(n -> unitService.updateMain(unit, user, n), eef);
  }

  @Override
  public Unit updateEdit(Unit unit, Concern concern, User user) {
    boolean isNew = unit.getId() == null;
    Unit u = NotifyList.execute(n -> unitService.updateEdit(unit, concern, user, n), eef);
    if (isNew) {
      containerWriteService.notifyRoot(concern);
    }
    return u;
  }

  @Override
  public List<Integer> batchCreate(BatchUnits batch, Concern concern, User user) {
    List<Integer> created = NotifyList.execute(n -> unitService.batchCreate(batch, concern, user, n), eef);
    containerWriteService.notifyRoot(concern);
    return created;
  }

  @Override
  public void remove(int unitId, User user) {
    Unit unit = unitService.doRemove(unitId, user);
    entityEventHandler.entityDeleted(unit.getId(), unit.getConcern().getId());
  }

  @Override
  public void sendHome(int unitId, User user) {
    NotifyList.executeVoid(n -> unitService.sendHome(unitId, user, n), eef);
  }

  @Override
  public void holdPosition(int unitId, User user) {
    NotifyList.executeVoid(n -> unitService.holdPosition(unitId, user, n), eef);
  }

  @Override
  public void standby(int unitId, User user) {
    NotifyList.executeVoid(n -> unitService.standby(unitId, user, n), eef);
  }

  @Override
  public void removeCrew(int unit_id, int user_id) {
    NotifyList.executeVoid(n -> unitService.removeCrew(unit_id, user_id, n), eef);
  }

  @Override
  public void addCrew(int unit_id, int user_id) {
    NotifyList.executeVoid(n -> unitService.addCrew(unit_id, user_id, n), eef);
  }

  @Override
  public int importUnits(String data, Concern concern, User user) {
    int imported = NotifyList.execute(n -> unitService.importUnits(data, concern, user, n), eef);
    containerWriteService.notifyRoot(concern);
    return imported;
  }

}
