package at.wrk.coceso.service;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface TaskSocketService {

  void changeState(int incident_id, int unit_id, TaskState state, User user);

}
