package at.wrk.coceso.service;

import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class TaskService {
    @Autowired
    IncidentService incidentService;

    @Autowired
    UnitService unitService;

    @Autowired
    TaskDao taskDao;

    @Autowired
    LogService log;

    private boolean assignUnit(int incident_id, int unit_id, TaskState state, Operator user) {
        Incident i = incidentService.getById(incident_id);
        Unit u = unitService.getById(unit_id);

        // Not in same Concern; HoldPosition and Standby can't be assigned to multiple Units
        if(!i.getConcern().equals(u.getConcern())) {
            Logger.warning("TaskService.assignUnit(): Unit and Incident in different Concerns!");
            return false;
        }

        if(i.getType().isSingleUnit() && i.getUnits().size() > 0) {
            Logger.debug("TaskService.assignUnit(): Tried to assign multiple Units to Single Unit Incident");
            return false;
        }

        // Auto-Detach from all SingleUnit Incidents and Relocation
        for(Integer incId : u.getIncidents().keySet()) {
            Incident inc = incidentService.getById(incId);
            if(inc.getType().isSingleUnit() || inc.getType() == IncidentType.Relocation) {
                Logger.debug("TaskService.assignUnit(): Auto-detach unit #" + unit_id + ", incident #" + incId);
                if(inc.getType() != IncidentType.Relocation) {
                    Logger.debug("TaskService.assignUnit(): Auto-set incident #" + incId + " to state 'Done'");
                    inc.setState(IncidentState.Done);
                }
                incidentService.update(inc, user);
                taskDao.remove(inc.getId(), u.getId());
            }
        }

        if(user != null) {
            log.logFull(user, LogEntryType.UNIT_ASSIGN, u.getConcern(), u, i, true);
        }

        return taskDao.add(incident_id, unit_id, state);

    }

    private boolean assignUnit(int incident_id, int unit_id, Operator user) {
        return assignUnit(incident_id, unit_id, TaskState.Assigned, user);
    }

    public void detachUnit(int incident_id, int unit_id, Operator user) {
        Incident i = incidentService.getById(incident_id);
        Unit u = unitService.getById(unit_id);

        if(!i.getConcern().equals(u.getConcern())) {  // Not in same Concern
            return;
        }

        if(user != null) {
            log.logFull(user, LogEntryType.UNIT_DETACH, i.getConcern(), u, i, true);
        }

        taskDao.remove(incident_id, unit_id);
    }

    public synchronized boolean changeState(int incident_id, int unit_id, TaskState state, Operator user) {
        Logger.debug("TaskService.changeState(): User " + user.getUsername() + " triggered update of unit #" + unit_id +
                " and incident #" + incident_id + " to TaskState '" + state + "'");

        Incident i = incidentService.getById(incident_id);
        Unit u = unitService.getById(unit_id);

        if(i == null || u == null) {
            Logger.debug("TaskService.changeState(): Combination not found. unit #" + unit_id + " ==null: " + (u == null) +
                    ", incident #" + incident_id + " ==null: " + (i == null));
            return false;
        }

        if(!i.getConcern().equals(u.getConcern())) {    // Not in same Concern
            Logger.warning("TaskService.changeState(): Combination in different concerns. unit #" + unit_id +
                    ", incident #" + incident_id);
            return false;
        }

        TaskState tmp = (i.getUnits() != null) ?
                i.getUnits().get(u.getId()) : null;

        // Assign Unit if TaskState is Assigned
        if(tmp == null && (state == TaskState.Assigned))  // Not Assigned
            assignUnit(incident_id, unit_id, user);
        else if(tmp == null) {
            Logger.debug("TaskService.changeState(): Unit not assigned, new State is not 'Assigned'. unit #" + unit_id +
                    ", incident #" + incident_id);
            return false;
        }

        if(!i.getType().isPossibleState(state)) {
            Logger.warning("TaskService.changeState(): new State not possible, cancel Request. unit #" + unit_id +
                    ", incident #" + incident_id);
            return false;
        }

        i.getUnits().put(unit_id, state);


        if(user != null) {
            log.logFull(user, LogEntryType.TASKSTATE_CHANGED, i.getConcern(), u, i, true);
        }

        switch (state) {
            case Assigned:
                if(i.getState() == IncidentState.New || i.getState() == IncidentState.Open) {
                    Incident wIncident = i.slimCopy();
                    wIncident.setState(IncidentState.Dispo);
                    log.logFull(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wIncident, true);
                    incidentService.update(wIncident);
                }
                break;
            case ABO:
                // TODO Auto-set Incident State to Working
                // Set Position of Unit to BO
                Unit writeUnit = u.slimCopy();
                writeUnit.setPosition(i.getBo());
                log.logFull(user, LogEntryType.UNIT_AUTOSET_POSITION, i.getConcern(), writeUnit, i, true);
                unitService.update(writeUnit);
                break;
            case ZAO:
                if(i.getType().isSingleUnit()) {
                    Incident writeIncident = i.slimCopy();
                    writeIncident.setState(IncidentState.Working);
                    log.logFull(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, writeIncident, true);
                    incidentService.update(writeIncident);
                }
                break;
            case AAO:
                // Set Position of Unit to AO
                Unit writeUnit2 = u.slimCopy();
                writeUnit2.setPosition(i.getAo());
                log.logFull(user, LogEntryType.UNIT_AUTOSET_POSITION, i.getConcern(), writeUnit2, i, true);
                unitService.update(writeUnit2);

                // If Relocation and at AO -> Change to HoldPosition
                if(i.getType() == IncidentType.Relocation) {
                    state = TaskState.Detached;

                    // If Relocation goes to unit.home -> just detach, so unit is marked as 'at Home'
                    if(!i.getAo().equals(u.getHome())) {
                        Incident hold = new Incident();
                        hold.setType(IncidentType.HoldPosition);
                        hold.setAo(i.getAo());
                        hold.setConcern(i.getConcern());
                        hold.setState(IncidentState.Working);

                        hold.setId(incidentService.add(hold));
                        assignUnit(hold.getId(), unit_id, TaskState.AAO, user);
                    }
                }

                if(i.getType() == IncidentType.ToHome) {
                    state = TaskState.Detached;
                }
                break;
            case Detached:
                if(i.getType().isSingleUnit()) {
                    Incident wInc = i.slimCopy();
                    wInc.setState(IncidentState.Done);
                    log.logFull(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wInc, true);
                    incidentService.update(wInc);
                }
                break;
            default:

                break;
        }

        if(state == TaskState.Detached) {
            taskDao.remove(incident_id, unit_id);
        } else {
            // TODO Avoid double call if was assigned in this call, BUT set to 'Assigned' must be possible, if assigned
            //      before
            taskDao.update(incident_id, unit_id, state);
        }

        if(user != null) {
            checkStates(incident_id, user);
        }
        return true;
    }

    public void checkStates(int incident_id, Operator user) {
        Incident i = incidentService.getById(incident_id);

        checkEmpty(i, user);

        Iterator<Integer> iterator = i.getUnits().keySet().iterator();

        while(iterator.hasNext()) {
            Integer unitId = iterator.next();

            if(i.getState() == IncidentState.Done) {
                log.logWithIDs(user.getId(), LogEntryType.UNIT_AUTO_DETACH, i.getConcern(), unitId, i.getId(), true);
                detachUnit(i.getId(), unitId, null);

                iterator.remove();
            } else {
                TaskState state = i.getUnits().get(unitId);
                if(state == TaskState.Detached) {
                    log.logWithIDs(user.getId(), LogEntryType.UNIT_AUTO_DETACH, i.getConcern(), unitId, i.getId(), true);
                    detachUnit(i.getId(), unitId, null);

                    iterator.remove();
                }
            }
        }

        checkEmpty(i, user);
    }

    private void checkEmpty(Incident i, Operator user) {
        // TODO Avoid that new or open incidents are closed (e.g. after undo of incorrect assigning)
        if(i.getUnits().isEmpty() && i.getState() != IncidentState.Done) {
            Incident write = i.slimCopy();
            write.setState(IncidentState.Done);
            log.logFull(user, LogEntryType.INCIDENT_AUTO_DONE, i.getConcern(), null, write, true);
            incidentService.update(write);
        }
    }

    public Map<Integer, TaskState> getAllByUnitId(int uid) {
        return taskDao.getAllByUnitId(uid);
    }

    public Map<Integer, TaskState> getAllByIncidentId(Integer iid) {
        return taskDao.getAllByIncidentId(iid);
    }
}
