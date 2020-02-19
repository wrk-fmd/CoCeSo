package at.wrk.coceso.service;

import at.wrk.coceso.entity.enums.TaskState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface TaskWriteService {

    /**
     * Changes the state of the given incident and unit combination to the specified state.
     */
    void changeState(int incidentId, int unitId, TaskState state);

    /**
     * Assigns the given unit to the incident. If the unit is already assigned, no operation is performed.
     */
    void assignUnit(int incidentId, int unitId);
}
