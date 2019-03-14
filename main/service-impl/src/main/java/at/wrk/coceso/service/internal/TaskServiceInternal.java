package at.wrk.coceso.service.internal;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface TaskServiceInternal {

  void assignUnit(int incidentId, int unitId, User user, NotifyList notify);

  void changeState(int incidentId, int unitId, TaskState state, User user, NotifyList notify);

  void changeState(Incident incident, Unit unit, TaskState state, User user, NotifyList notify);

  void uncheckedChangeState(Incident incident, Unit unit, TaskState state, User user, NotifyList notify);

}
