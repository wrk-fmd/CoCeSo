package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import at.wrk.coceso.service.ContainerService;
import at.wrk.coceso.service.ContainerSocketService;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ContainerSocketServiceImpl implements ContainerSocketService {

  @Autowired
  private ContainerService containerService;

  private final EntityEventHandler<Container> entityEventHandler;
  private final EntityEventListener<Container> entityEventListener;

  @Autowired
  public ContainerSocketServiceImpl(SocketMessagingTemplate template) {
    entityEventHandler = EntityEventHandler.getInstance(Container.class);
    entityEventListener = entityEventHandler.addListener(new WebSocketWriter<>(template, "/topic/container/%d", null, null));
  }

  @PreDestroy
  public void destroy() {
    entityEventHandler.removeListener(entityEventListener);
  }

  @Override
  public synchronized Container update(Container container, Concern concern) {
    container = containerService.doUpdate(container, concern);
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

    entityEventHandler.entityChanged(pair.newcont, pair.newcont.getConcern().getId());
    if (pair.notifyRoot) {
      notifyRoot(pair.newcont.getConcern());
    }
    if (pair.previous != null) {
      entityEventHandler.entityChanged(pair.previous, pair.newcont.getConcern().getId());
    }
  }

  @Override
  public void removeUnit(int unitId) {
    Container cont = containerService.doRemoveUnit(unitId);
    if (cont != null) {
      notifyRoot(cont.getConcern());
      entityEventHandler.entityChanged(cont, cont.getConcern().getId());
    }
  }

  @Override
  public void notifyRoot(Concern concern) {
    entityEventHandler.entityChanged(containerService.getRoot(concern), concern.getId());
  }

}
