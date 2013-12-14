package at.wrk.coceso.service;

import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.utils.LogText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {

    @Autowired
    LogService logService;

    @Autowired
    UnitDao unitDao;

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
        logService.logFull(operator, LogText.UNIT_UPDATE+": "+unit.getCall(), unit.concern, unit, null, true);
        return ret;
    }

    public boolean updateFull(Unit unit) {
        return unitDao.updateFull(unit);
    }

    public boolean updateFull(Unit unit, Operator operator) {
        boolean ret = updateFull(unit);
        logService.logFull(operator, LogText.UNIT_UPDATE+": "+unit.getCall(), unit.concern, unit, null, true);
        return ret;
    }

    public int add(Unit unit) {
        return unitDao.add(unit);
    }

    public int add(Unit unit, Operator operator) {
        int ret = add(unit);
        unit.id = ret;
        logService.logFull(operator, LogText.UNIT_NEW+": "+unit.getCall(), unit.concern, unit, null, true);
        return ret;
    }

    public boolean remove(Unit unit) {
        return unitDao.remove(unit);
    }

    public boolean remove(Unit unit, Operator operator) {
        if(unit == null)
            return false;
        logService.logFull(operator, LogText.UNIT_DELETE+": "+unit.getCall(), unit.concern, unit, null, true);
        return remove(unit);
    }
}
