package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.TaskWriteService;
import at.wrk.coceso.service.internal.TaskServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TaskWriteServiceImpl implements TaskWriteService {

  @Autowired
  private TaskServiceInternal taskService;

  @Autowired
  private EntityEventFactory entityEventFactory;

  @Override
  public synchronized void changeState(int incidentId, int unitId, TaskState state) {
    NotifyList.executeVoid(n -> taskService.changeState(incidentId, unitId, state, n), entityEventFactory);
  }

  @Override
  public void assignUnit(final int incidentId, final int unitId) {
    NotifyList.executeVoid(notifyList -> taskService.assignUnit(incidentId, unitId, notifyList), entityEventFactory);
  }
}
