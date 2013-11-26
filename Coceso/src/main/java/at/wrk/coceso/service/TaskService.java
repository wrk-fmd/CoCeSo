package at.wrk.coceso.service;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.*;
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

    @Autowired
    LogService log;

    public boolean assignUnit(int incident_id, int unit_id, Person user) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        if(!i.aCase.equals(u.aCase) || i.type == IncidentType.HoldPosition || i.type == IncidentType.Standby) {
            return false;
        }

        if(user != null) {
            log.logFull(user, "Unit assigned by User", u.aCase.id, u, i, true);
        }

        return taskDao.add(incident_id, unit_id, TaskState.Assigned);

    }

    public void detachUnit(int incident_id, int unit_id, Person user) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        if(!i.aCase.equals(u.aCase)) {
            return;
        }

        if(user != null) {
            log.logFull(user, "Unit detached by User", i.aCase.id, u, i, true);
        }

        taskDao.remove(incident_id, unit_id);
    }

    public void changeState(int incident_id, int unit_id, TaskState state, Person user) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        TaskState tmp = i != null && i.units != null && u != null ?
                i.units.get(u.id) : null;

        if(tmp == null)
            return;

        i.units.put(unit_id, state);

        if(!i.aCase.equals(u.aCase)) {
            return;
        }

        if(user != null) {
            log.logFull(user, "TaskState changed", i.aCase.id, u, i, true);
        }

        taskDao.update(incident_id, unit_id, state);

    }

}
