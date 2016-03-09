package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.TaskSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TaskSocketServiceImpl implements TaskSocketService {

  @Autowired
  private TaskService taskService;

  @Override
  public synchronized void changeState(int incident_id, int unit_id, TaskState state, User user) {
    NotifyList.executeVoid(n -> taskService.changeState(incident_id, unit_id, state, user, n));
  }

}
