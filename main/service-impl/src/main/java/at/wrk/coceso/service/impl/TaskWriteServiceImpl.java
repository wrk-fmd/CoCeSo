package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.TaskServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.TaskWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TaskWriteServiceImpl implements TaskWriteService {

  @Autowired
  private TaskServiceInternal taskService;

  @Autowired
  private EntityEventFactory eef;

  @Override
  public synchronized void changeState(int incident_id, int unit_id, TaskState state, User user) {
    NotifyList.executeVoid(n -> taskService.changeState(incident_id, unit_id, state, user, n), eef);
  }

}
