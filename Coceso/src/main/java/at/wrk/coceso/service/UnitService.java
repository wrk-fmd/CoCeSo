package at.wrk.coceso.service;

import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UnitService {

    @Autowired
    LogService logService;

    @Autowired
    UnitDao unitDao;

    @Autowired
    TaskDao taskDao;

    @Autowired
    IncidentService incidentService;

    @Autowired
    TaskService taskService;

    public Unit getById(int id) {
        return unitDao.getById(id);
    }


    public List<Unit> getAll(int case_id) {
        return unitDao.getAll(case_id);
    }


    public boolean update(Unit unit) {
        return unitDao.update(unit);
    }

    public boolean update(Unit unit, Operator operator) {
        boolean ret = update(unit);
        logService.logFull(operator, LogEntryType.UNIT_UPDATE, unit.getConcern(), unit, null, true);
        return ret;
    }

    public boolean updateFull(Unit unit) {
        return unitDao.updateFull(unit);
    }

    public boolean updateFull(Unit unit, Operator operator) {
        boolean ret = updateFull(unit);
        // UNIT_CREATE for difference in Log -> so it can be deleted, if Unit is only updated via Edit Page
        logService.logFull(operator, LogEntryType.UNIT_CREATE, unit.getConcern(), unit, null, true);
        return ret;
    }

    public int add(Unit unit) {
        return unitDao.add(unit);
    }

    public int add(Unit unit, Operator operator) {

        unit.setId(add(unit));
        logService.logFull(operator, LogEntryType.UNIT_CREATE, unit.getConcern(), unit, null, true);
        return unit.getId();
    }

    public boolean remove(Unit unit) {
        return unitDao.remove(unit);
    }

    public boolean remove(Unit unit, Operator operator) {
        if(unit == null)
            return false;
        // Changed to REMOVED Flag on LogEntry:Create
        //logService.logFull(operator, LogEntryType.UNIT_DELETE, unit.getConcern(), unit, null, true);
        return remove(unit);
    }

    private boolean detachAllGivenTypes(int unitId, IncidentType... types) {
        List<Incident> list = taskDao.getAllByUnitIdWithType(unitId);

        List<IncidentType> lTypes = Arrays.asList(types);

        for(Incident i : list) {
            if(!lTypes.contains(i.getType()))
                return false;
        }

        Unit unit = getById(unitId);

        for(Incident i : list) {
            i.setState(IncidentState.Done);

            incidentService.update(i);
            taskDao.remove(i.getId(), unitId);
        }
        return true;
    }

    private Incident createSingleUnitIncident(IncidentType type, int activeCase, Operator user) {
        Incident ret = new Incident();

        ret.setState(IncidentState.Dispo);
        ret.setConcern(activeCase);
        ret.setType(type);
        ret.setCaller(user.getUsername());

        return ret;
    }

    public boolean sendHome(int activeCase, int unitId, Operator user) {
        if(!detachAllGivenTypes(unitId, IncidentType.Standby, IncidentType.HoldPosition))
            return false;

        Unit unit = getById(unitId);

        Incident toHome = createSingleUnitIncident(IncidentType.ToHome, activeCase, user);

        toHome.setAo(unit.getHome());
        toHome.setBo(unit.getPosition());


        toHome.setId(incidentService.add(toHome, user));
        //log.logFull(user, LogText.SEND_HOME_ASSIGN, activeCase, unit, toHome, true);
        taskService.changeState(toHome.getId(), unitId, TaskState.Assigned, user);

        return true;
    }

    public boolean holdPosition(int activeCase, int unitId, Operator user) {
        // HoldPosition only possible, if no other Incident assigned.
        if(!detachAllGivenTypes(unitId))
            return false;

        Unit unit = getById(unitId);

        Incident inc = createSingleUnitIncident(IncidentType.HoldPosition, activeCase, user);

        inc.setAo(unit.getPosition());

        inc.setId(incidentService.add(inc, user));
        //log.logFull(user, LogText.SEND_HOME_ASSIGN, activeCase, unit, toHome, true);
        taskService.changeState(inc.getId(), unitId, TaskState.Assigned, user);

        return true;
    }

    public boolean standby(int activeCase, int unitId, Operator user) {
        // Standby only possible, if no other Incident assigned, except for ToHome
        if(!detachAllGivenTypes(unitId, IncidentType.ToHome, IncidentType.HoldPosition))
            return false;

        Unit unit = getById(unitId);

        Incident inc = createSingleUnitIncident(IncidentType.Standby, activeCase, user);

        inc.setAo(unit.getPosition());

        inc.setId(incidentService.add(inc, user));
        //log.logFull(user, LogText.SEND_HOME_ASSIGN, activeCase, unit, toHome, true);
        taskService.changeState(inc.getId(), unitId, TaskState.Assigned, user);

        return true;
    }

    public Set<Integer> getNonDeletable(int caseId) {
        return unitDao.getNonDeletable(caseId);
    }
}
