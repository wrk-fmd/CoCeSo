package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyListExecutor;
import at.wrk.coceso.service.TaskWriteService;
import at.wrk.coceso.service.internal.TaskServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TaskWriteServiceImpl implements TaskWriteService {

  private final TaskServiceInternal taskService;
  private final NotifyListExecutor notifyListExecutor;

  @Autowired
  public TaskWriteServiceImpl(final TaskServiceInternal taskService, final NotifyListExecutor notifyListExecutor) {
    this.taskService = taskService;
    this.notifyListExecutor = notifyListExecutor;
  }

  @Override
  public synchronized void changeState(int incidentId, int unitId, TaskState state) {
    notifyListExecutor.executeVoid(n -> taskService.changeState(incidentId, unitId, state, n));
  }

  @Override
  public void assignUnit(final int incidentId, final int unitId) {
    notifyListExecutor.executeVoid(notifyList -> taskService.assignUnit(incidentId, unitId, notifyList));
  }
}
