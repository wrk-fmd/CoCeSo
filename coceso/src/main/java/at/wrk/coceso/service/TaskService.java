package at.wrk.coceso.service;

import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.JsonContainer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class TaskService {

  private final static Logger LOG = Logger.getLogger(TaskService.class);

  @Autowired
  IncidentService incidentService;

  @Autowired
  UnitService unitService;

  @Autowired
  TaskDao taskDao;

  @Autowired
  LogService logService;

  private boolean assignUnit(int incident_id, int unit_id, TaskState state, Operator user) {
    Incident i = incidentService.getById(incident_id);
    Unit u = unitService.getById(unit_id);

    // Method is called from controlled environment (changeState), don't need to check for same concern etc anymore
    // HoldPosition and Standby can't be assigned to multiple Units
    if (i.getType().isSingleUnit() && i.getUnits().size() > 0) {
      LOG.debug("TaskService.assignUnit(): Tried to assign multiple Units to Single Unit Incident");
      return false;
    }

    // Auto-Detach from all SingleUnit Incidents and Relocation
    // TODO Logging
    for (Integer incId : u.getIncidents().keySet()) {
      Incident inc = incidentService.getById(incId);
      if (inc.getType().isSingleUnit() || inc.getType() == IncidentType.Relocation) {
        LOG.debug(String.format("TaskService.assignUnit(): Auto-detach unit #%d, incident #%d", u.getId(), incId));
        if (inc.getType() != IncidentType.Relocation) {
          LOG.debug(String.format("TaskService.assignUnit(): Auto-set incident #%d to state 'Done'", incId));
          inc.setState(IncidentState.Done);
          incidentService.update(inc, user);
        }
        taskDao.remove(inc.getId(), u.getId());
      }
    }

    logService.logAuto(user, LogEntryType.UNIT_ASSIGN, u.getConcern(), u, i, state);
    return taskDao.add(i.getId(), u.getId(), state);
  }

  private void detachUnit(int incident_id, int unit_id, Operator user) {
    Incident i = incidentService.getById(incident_id);
    Unit u = unitService.getById(unit_id);

    if (!i.getConcern().equals(u.getConcern())) {  // Not in same Concern
      return;
    }

    if (user != null) {
      logService.logAuto(user, LogEntryType.UNIT_DETACH, i.getConcern(), u, i, TaskState.Detached);
    }

    taskDao.remove(incident_id, unit_id);
  }

  public synchronized boolean changeState(int incident_id, int unit_id, TaskState state, Operator user) {
    if (user == null) {
      LOG.error("TaskService.changeState() called without user!");
      return false;
    }

    LOG.debug(String.format("TaskService.changeState(): User %s triggered update of unit #%d and incident #%d to '%s'",
            user.getUsername(), unit_id, incident_id, state));

    Incident i = incidentService.getById(incident_id);
    Unit u = unitService.getById(unit_id);

    if (i == null || u == null) {
      LOG.info(String.format("TaskService.changeState(): Unit #%d/incident #%d not found",
              unit_id, incident_id));
      return false;
    }

    if (!i.getConcern().equals(u.getConcern())) {    // Not in same Concern
      LOG.info(String.format("TaskService.changeState(): Combination Unit #%d/incident #%d in different concerns",
              u.getId(), i.getId()));
      return false;
    }

    if (!i.getType().isPossibleState(state)) {
      LOG.warn(String.format("TaskService.changeState(): New state not possible for Unit #%d/incident #%d",
              u.getId(), i.getId()));
      return false;
    }

    TaskState tmp = (i.getUnits() != null) ? i.getUnits().get(u.getId()) : null;
    if (tmp == null) {
      if (state == TaskState.Detached) {
        // We are detaching, but unit isn't assigned anyway: Nothing to do anymore
        return true;
      }
      // Not assigned yet, assign with chosen state
      if (!assignUnit(i.getId(), u.getId(), state, user)) {
        return false;
      }
    }

    boolean hasWorking = false;
    for (Map.Entry<Integer, TaskState> entry : i.getUnits().entrySet()) {
      if (entry.getKey() == u.getId()) {
        continue;
      }
      if (entry.getValue().isWorking()) {
        hasWorking = true;
      }
    }

    if (tmp != null) {
      // Log only if not already done by assignment
      logService.logAuto(user, LogEntryType.TASKSTATE_CHANGED, i.getConcern(), u, i, state);
    }

    LOG.debug("state = " + state);
    switch (state) {
      case Assigned:
      case ZBO:
        if (i.getState() != IncidentState.Dispo && !hasWorking) {
          // Has no units in working, so set incident to dispo
          Incident wIncident = i.slimCopy();
          wIncident.setState(IncidentState.Dispo);
          logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wIncident, state, new JsonContainer("incident", wIncident.changesState(i)));
          incidentService.update(wIncident);
        }
        break;
      case ABO:
        if (i.getState() != IncidentState.Working) {
          // Set incident to working
          Incident wIncident = i.slimCopy();
          wIncident.setState(IncidentState.Working);
          logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wIncident, state, new JsonContainer("incident", wIncident.changesState(i)));
          incidentService.update(wIncident);
        }

        // Set Position of Unit to BO
        Unit writeUnit = u.slimCopy();
        writeUnit.setPosition(i.getBo());
        logService.logAuto(user, LogEntryType.UNIT_AUTOSET_POSITION, i.getConcern(), writeUnit, i, state, new JsonContainer("unit", writeUnit.changesPosition(u)));
        unitService.update(writeUnit);
        break;
      case ZAO:
        if (i.getState() != IncidentState.Working) {
          // Set incident to working
          Incident wIncident = i.slimCopy();
          wIncident.setState(IncidentState.Working);
          logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wIncident, state, new JsonContainer("incident", wIncident.changesState(i)));
          incidentService.update(wIncident);
        }
        break;
      case AAO:
        // Set Position of Unit to AO
        Unit writeUnit2 = u.slimCopy();
        writeUnit2.setPosition(i.getAo());
        logService.logAuto(user, LogEntryType.UNIT_AUTOSET_POSITION, i.getConcern(), writeUnit2, i, state, new JsonContainer("unit", writeUnit2.changesPosition(u)));
        unitService.update(writeUnit2);

        if (i.getType() == IncidentType.Relocation) {
          // If Relocation and at AO -> Change to HoldPosition
          state = TaskState.Detached;

          // If Relocation goes to unit.home -> just detach, so unit is marked as 'at Home'
          if (!i.getAo().equals(u.getHome())) {
            Incident hold = new Incident();
            hold.setType(IncidentType.HoldPosition);
            hold.setAo(i.getAo());
            hold.setConcern(i.getConcern());
            hold.setState(IncidentState.Working);
            hold.setId(incidentService.add(hold));

            // We don't need the whole changeState logic here (at least for now), so don't call it
            assignUnit(hold.getId(), u.getId(), TaskState.AAO, user);
          }
        } else if (i.getType() == IncidentType.ToHome) {
          state = TaskState.Detached;
        } else if (i.getState() != IncidentState.Working) {
          Incident wIncident = i.slimCopy();
          wIncident.setState(IncidentState.Working);
          logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wIncident, state, new JsonContainer("incident", wIncident.changesState(i)));
          incidentService.update(wIncident);
        }
        break;
      case Detached:
        if (i.getType().isSingleUnit()) {
          Incident wInc = i.slimCopy();
          wInc.setState(IncidentState.Done);
          logService.logAuto(user, LogEntryType.INCIDENT_AUTO_STATE, i.getConcern(), u, wInc, state, new JsonContainer("incident", wInc.changesState(i)));
          incidentService.update(wInc);
        }
        break;
      default:
        LOG.warn("!!! Unknown TaskState !!!");
        break;
    }

    if (state == TaskState.Detached) {
      LOG.debug("Try to detach Unit");
      taskDao.remove(i.getId(), u.getId());
    } else if (tmp != null) {
      // Only save if not already done by assignUnit
      LOG.debug("Try to update TaskState");
      taskDao.update(i.getId(), u.getId(), state);
    }

    checkStates(i.getId(), user);
    return true;
  }

  /**
   * Deletes Relations of all Units in TaskState 'Detached'. Calls #checkEmpty(Incident, Operator) to close the Incident
   * if no Units are attached anymore
   *
   * @param incident_id ID of Incident, that will be checked
   * @param user must not be null. Operator to write LogEntries
   */
  public void checkStates(int incident_id, Operator user) {
    Incident i = incidentService.getById(incident_id);

    checkEmpty(i, user);

    Iterator<Integer> iterator = i.getUnits().keySet().iterator();

    while (iterator.hasNext()) {
      Integer unitId = iterator.next();

      if (i.getState() == IncidentState.Done) {
        logService.logAuto(user, LogEntryType.UNIT_AUTO_DETACH, i.getConcern(), new Unit(unitId), i, TaskState.Detached);
        detachUnit(i.getId(), unitId, null);

        iterator.remove();
      } else {
        TaskState state = i.getUnits().get(unitId);
        if (state == TaskState.Detached) {
          logService.logAuto(user, LogEntryType.UNIT_AUTO_DETACH, i.getConcern(), new Unit(unitId), i, TaskState.Detached);
          detachUnit(i.getId(), unitId, null);

          iterator.remove();
        }
      }
    }

    checkEmpty(i, user);
  }

  /**
   * Sets State of Incident to 'Done' if no Units are attached
   *
   * @param i Incident to check
   * @param user Operator to write LogEntries
   */
  private void checkEmpty(Incident i, Operator user) {
    // TODO Avoid that new or open incidents are closed (e.g. after undo of incorrect assigning)
    if (i.getUnits().isEmpty() && i.getState() != IncidentState.Done) {
      Incident write = i.slimCopy();
      write.setState(IncidentState.Done);
      logService.logAuto(user, LogEntryType.INCIDENT_AUTO_DONE, i.getConcern(), null, write, new JsonContainer("incident", write.changesState(i)));
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
