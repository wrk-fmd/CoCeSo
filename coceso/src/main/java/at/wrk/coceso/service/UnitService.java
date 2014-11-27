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
import at.wrk.coceso.entity.helper.ChangePair;
import at.wrk.coceso.entity.helper.JsonContainer;
import at.wrk.coceso.entity.helper.UnitWithLocked;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UnitService {

    private final static
    Logger LOG = Logger.getLogger(UnitService.class);

    @Autowired
    private LogService logService;

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TaskService taskService;

    public Unit getById(int id) {
        return unitDao.getById(id);
    }


    public List<Unit> getAll(int case_id) {
        return unitDao.getAll(case_id);
    }

    public List<UnitWithLocked> getAllWithLocked(int concernId) {
      return unitDao.getAllWithLocked(concernId);
    }

    public boolean update(Unit unit) {
        return unitDao.update(unit);
    }

    public boolean update(Unit unit, Operator operator) {
        Map<String, ChangePair<Object>> changes = unit.changes(getById(unit.getId()));
        boolean ret = update(unit);
        if(!ret) {
            LOG.info(String.format("Update of Unit #%d by User %s failed",
                    unit.getId(), operator.getUsername()));
        }
        logService.logAuto(operator, LogEntryType.UNIT_UPDATE, unit.getConcern(), unit, null, new JsonContainer("unit", changes));
        return ret;
    }

    public boolean updateFull(Unit unit) {
        return unitDao.updateFull(unit);
    }

    public boolean updateFull(Unit unit, Operator operator) {
        Map<String, ChangePair<Object>> changes = unit.changesFull(getById(unit.getId()));
        boolean ret = updateFull(unit);
        if(!ret) {
            LOG.info(String.format("Full Update of Unit #%d by User %s failed",
                    unit.getId(), operator.getUsername()));
        }
        // UNIT_CREATE for difference in Log -> so it can be deleted, if Unit is only updated via Edit Page
        logService.logAuto(operator, LogEntryType.UNIT_CREATE, unit.getConcern(), unit, null, new JsonContainer("unit", changes));
        return ret;
    }

    public int add(Unit unit) {
        return unitDao.add(unit);
    }

    public int add(Unit unit, Operator operator) {
        unit.setId(add(unit));
        logService.logAuto(operator, LogEntryType.UNIT_CREATE, unit.getConcern(), unit, null, new JsonContainer("unit", unit.changesFull(null)));
        return unit.getId();
    }

    public boolean remove(int unitId) {
        return unitDao.remove(unitId);
    }

    public boolean remove(int unitId, Operator operator) {
        // Changed to REMOVED Flag on LogEntry:Create
        return remove(unitId);
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
        taskService.changeState(inc.getId(), unitId, TaskState.Assigned, user);

        return true;
    }
}
