package at.wrk.coceso.service;


import at.wrk.coceso.dao.CocesoDao;
import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcernService {

    @Autowired
    private ConcernDao concernDao;

    @Autowired
    private LogService logService;

    public Concern getById(int id) {
        return concernDao.getById(id);
    }

    public List<Concern> getAll() {
        return concernDao.getAll();
    }

    public boolean update(Concern concern, Operator user) {
        if(concern == null) {
            return false;
        }

        // Return false if Name changed and another Concern already has the same Name
        if(!concernDao.getById(concern.getId()).getName().equals(concern.getName()) && nameAlreadyExists(concern.getName())) {
            return false;
        }

        logService.logFull(user, LogEntryType.CONCERN_UPDATE, concern.getId(), null, null, true);
        return concernDao.update(concern);
    }

    public int add(Concern concern, Operator user) {
        if(concern == null || nameAlreadyExists(concern.getName()))
        {
            Logger.debug("ConcernService.add(): Error on Concern create. Code -3");
            return -3;
        }
        concern.setId(concernDao.add(concern));
        logService.logFull(user, LogEntryType.CONCERN_CREATE, concern.getId(), null, null, true);
        return concern.getId();
    }

    /*
     * Returns true if name is empty, only whitespaces or another Incident already uses this Name
     */
    private boolean nameAlreadyExists(String name) {
        if(name == null || name.isEmpty() || name.trim().isEmpty()) {
            return true;
        }
        List<Concern> list = concernDao.getAll();

        for(Concern c : list) {
            if(c.getName() != null && name.equals(c.getName()))
                return true;
        }
        return false;
    }

    // TODO if used anywhere, fix foreign key problem on delete
    @Deprecated
    public boolean remove(Concern concern, Operator user) {
        logService.logFull(user, LogEntryType.CONCERN_REMOVE, concern.getId(), null, null, true);
        return concernDao.remove(concern);
    }

    public List<Concern> getAllActive() {

        return concernDao.getAllActive();
    }
}
