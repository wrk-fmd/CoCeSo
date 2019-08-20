package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.ContainerWriteService;
import at.wrk.coceso.service.UnitWriteService;
import at.wrk.coceso.service.internal.UnitServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

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
  public Unit updateMain(final Unit unit) {
    return NotifyList.execute(n -> unitService.updateMain(unit, n), eef);
  }

  @Override
  public Unit updateEdit(final Unit unit, final Concern concern) {
    boolean isNew = unit.getId() == null;
    Unit u = NotifyList.execute(n -> unitService.updateEdit(unit, concern, n), eef);
    if (isNew) {
      containerWriteService.notifyRoot(concern);
    }
    return u;
  }

  @Override
  public List<Integer> batchCreate(final BatchUnits batch, final Concern concern) {
    Optional.ofNullable(batch.getHome())
            .ifPresent(Point::tryToResolveExternalData);
    List<Integer> created = NotifyList.execute(n -> unitService.batchCreate(batch, concern, n), eef);
    containerWriteService.notifyRoot(concern);
    return created;
  }

  @Override
  public void remove(final int unitId) {
    Unit unit = unitService.doRemove(unitId);
    entityEventHandler.entityDeleted(unit.getId(), unit.getConcern().getId());
  }

  @Override
  public void sendHome(final int unitId) {
    NotifyList.executeVoid(n -> unitService.sendHome(unitId, n), eef);
  }

  @Override
  public void holdPosition(final int unitId) {
    NotifyList.executeVoid(n -> unitService.holdPosition(unitId, n), eef);
  }

  @Override
  public void standby(final int unitId) {
    NotifyList.executeVoid(n -> unitService.standby(unitId, n), eef);
  }

  @Override
  public void removeCrew(final int unitId, final int userId) {
    NotifyList.executeVoid(n -> unitService.removeCrew(unitId, userId, n), eef);
  }

  @Override
  public void addCrew(final int unitId, final int userId) {
    NotifyList.executeVoid(n -> unitService.addCrew(unitId, userId, n), eef);
  }

  @Override
  public int importUnits(final String data, final Concern concern) {
    int imported = NotifyList.execute(n -> unitService.importUnits(data, concern, n), eef);
    containerWriteService.notifyRoot(concern);
    return imported;
  }
}
