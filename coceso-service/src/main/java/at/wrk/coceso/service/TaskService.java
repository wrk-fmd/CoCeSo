package at.wrk.coceso.service;

import at.wrk.coceso.dto.task.TaskStateDto;
import at.wrk.coceso.dto.task.TaskUpdateDto;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;

public interface TaskService {

    void assignUnit(Incident incident, Unit unit);

    void changeState(Incident incident, Unit unit, TaskUpdateDto data);

    void detachAll(Incident incident, boolean auto);

    void sendHome(Unit unit);

    void holdPosition(Unit unit);

    void standby(Unit unit);
}
