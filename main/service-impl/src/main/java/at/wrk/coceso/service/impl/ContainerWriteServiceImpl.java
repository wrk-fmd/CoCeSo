package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.service.ContainerService;
import at.wrk.coceso.service.ContainerWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ContainerWriteServiceImpl implements ContainerWriteService {

  @Autowired
  private ContainerService containerService;

  private final EntityEventHandler<Container> entityEventHandler;
  private final EntityEventListener<Container> entityEventListener;

  @Autowired
  public ContainerWriteServiceImpl(EntityEventFactory eef) {
    entityEventHandler = eef.getEntityEventHandler(Container.class);
    entityEventListener = eef.getWebSocketWriter("/topic/container/%d", null, null);
    entityEventHandler.addListener(entityEventListener);
  }

  @PreDestroy
  public void destroy() {
    entityEventHandler.removeListener(entityEventListener);
  }

  @Override
  public synchronized Container update(Container container, Concern concern) {
    container = containerService.doUpdate(container, concern);
    setSpare(container);
    entityEventHandler.entityChanged(container, container.getConcern().getId());
    return container;
  }

  @Override
  public synchronized void remove(int containerId) {
    Container container = containerService.doRemove(containerId);
    entityEventHandler.entityDeleted(containerId, container.getConcern().getId());
    notifyRoot(container.getConcern());
  }

  @Override
  public synchronized void updateUnit(int containerId, int unitId, double ordering) {
    ContainerService.ContainerPair pair = containerService.doUpdateUnit(containerId, unitId, ordering);

    setSpare(pair.newcont);
    entityEventHandler.entityChanged(pair.newcont, pair.newcont.getConcern().getId());

    if (pair.notifyRoot) {
      notifyRoot(pair.newcont.getConcern());
    }

    if (pair.previous != null) {
      setSpare(pair.previous);
      entityEventHandler.entityChanged(pair.previous, pair.newcont.getConcern().getId());
    }
  }

  @Override
  public void removeUnit(int unitId) {
    Container cont = containerService.doRemoveUnit(unitId);
    if (cont != null) {
      notifyRoot(cont.getConcern());
      setSpare(cont);
      entityEventHandler.entityChanged(cont, cont.getConcern().getId());
    }
  }

  @Override
  public void notifyRoot(Concern concern) {
    entityEventHandler.entityChanged(containerService.getRoot(concern), concern.getId());
  }

  private void setSpare(Container container) {
    if (container.getParent() == null) {
      container.setSpare(containerService.getSpare(container.getConcern()));
    }
  }

}
