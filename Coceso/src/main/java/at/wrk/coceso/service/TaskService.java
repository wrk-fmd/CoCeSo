package at.wrk.coceso.service;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.Incident;
import at.wrk.coceso.entities.TaskState;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Autowired
    IncidentDao incidentDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    TaskDao taskDao;

    public boolean assignUnit(int incident_id, int unit_id) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        if(!i.aCase.equals(u.aCase)) {
            return false;
        }

        return taskDao.add(incident_id, unit_id, TaskState.Assigned);

    }

    public void detachUnit(int incident_id, int unit_id) {
        taskDao.remove(incident_id, unit_id);
    }

    public void changeState(int incident_id, int unit_id, TaskState state) {
        //TODO
    }

    //TODO sendHome, setToHome
}
