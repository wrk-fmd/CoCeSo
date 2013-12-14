package at.wrk.coceso.service;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.utils.LogText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public boolean assignUnit(int incident_id, int unit_id, Operator user) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        // Not in same Concern; HoldPosition and Standby can't be assigned to multiple Units
        if(!i.concern.equals(u.concern) || i.type == IncidentType.HoldPosition || i.type == IncidentType.Standby) {
            return false;
        }

        if(user != null) {
            log.logFull(user, LogText.UNIT_ASSIGN, u.concern, u, i, true);
        }

        return taskDao.add(incident_id, unit_id, TaskState.Assigned);

    }

    public void detachUnit(int incident_id, int unit_id, Operator user) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        if(!i.concern.equals(u.concern)) {  // Not in same Concern
            return;
        }

        if(user != null) {
            log.logFull(user, LogText.UNIT_DETACH, i.concern, u, i, true);
        }

        taskDao.remove(incident_id, unit_id);
    }

    public void changeState(int incident_id, int unit_id, TaskState state, Operator user) {
        Incident i = incidentDao.getById(incident_id);
        Unit u = unitDao.getById(unit_id);

        TaskState tmp = i != null && i.units != null && u != null ?
                i.units.get(u.id) : null;

        if(tmp == null)  // Not Assigned
            return;

        i.units.put(unit_id, state);

        if(!i.concern.equals(u.concern)) {    // Not in same Concern
            return;
        }

        if(user != null) {
            log.logFull(user, LogText.UNIT_TASKSTATE_CHANGED, i.concern, u, i, true);
        }

        taskDao.update(incident_id, unit_id, state);

        switch (state) {
            case Assigned:
                if(i.state == IncidentState.New) {
                    Incident wIncident = i.slimCopy();
                    wIncident.state = IncidentState.Dispo;
                    log.logFull(user, LogText.INCIDENT_AUTO_STATE, i.concern, u, wIncident, true);
                    incidentDao.update(wIncident);
                }
                break;
            case ABO:
                Unit writeUnit = u.slimCopy();
                writeUnit.position = i.bo;
                log.logFull(user, LogText.UNIT_AUTOSET_POSITION, i.concern, writeUnit, i, true);
                unitDao.update(writeUnit);
                break;
            case ZAO:
                if(i.type == IncidentType.Relocation) {
                    Incident writeIncident = i.slimCopy();
                    writeIncident.state = IncidentState.Working;
                    log.logFull(user, LogText.INCIDENT_AUTO_STATE, i.concern, u, writeIncident, true);
                    incidentDao.update(writeIncident);
                }
                break;
            case AAO:
                Unit writeUnit2 = u.slimCopy();
                writeUnit2.position = i.ao;
                log.logFull(user, LogText.UNIT_AUTOSET_POSITION, i.concern, writeUnit2, i, true);
                unitDao.update(writeUnit2);

                break;
            case Detached:
                if(i.type == IncidentType.Standby || i.type == IncidentType.HoldPosition) {
                    Incident wInc = i.slimCopy();
                    wInc.state = IncidentState.Done;
                    log.logFull(user, LogText.INCIDENT_AUTO_STATE, i.concern, u, wInc, true);
                    incidentDao.update(wInc);
                }
                break;
            default:
                break;
        }

        if(user != null) {
            checkStates(incident_id, user);
        }
    }

    public void checkStates(int incident_id, Operator user) {
        Incident i = incidentDao.getById(incident_id);

        checkEmpty(i, user);

        for(Integer unitId : i.units.keySet()) {
            if(i.state == IncidentState.Done) {
                log.logWithIDs(user.id, LogText.UNIT_AUTO_DETACH, i.concern, unitId, i.id, true);
                detachUnit(i.id, unitId, null);
                i.units.remove(unitId);
            } else {
                TaskState state = i.units.get(unitId);
                if(state == TaskState.Detached) {
                    log.logWithIDs(user.id, LogText.UNIT_AUTO_DETACH, i.concern, unitId, i.id, true);
                    detachUnit(i.id, unitId, null);
                    i.units.remove(unitId);
                }
            }
        }

        checkEmpty(i, user);
    }

    private void checkEmpty(Incident i, Operator user) {
        if(i.units.isEmpty() && i.state != IncidentState.Done) {
            Incident write = i.slimCopy();
            write.state = IncidentState.Done;
            log.logFull(user, LogText.INCIDENT_NO_UNIT_ATTACHED, i.concern, null, write, true);
            incidentDao.update(write);
        }
    }

    public Map<Integer, TaskState> getAllByUnitId(int uid) {
        return taskDao.getAllByUnitId(uid);
    }

    public Map<Integer, TaskState> getAllByIncidentId(Integer iid) {
        return taskDao.getAllByIncidentId(iid);
    }
}
