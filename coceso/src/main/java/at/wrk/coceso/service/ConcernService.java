package at.wrk.coceso.service;


import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.LogEntryType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcernService {

    private final static
    Logger LOG = Logger.getLogger(ConcernService.class);

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
            LOG.warn("Given Concern is null. Aborting. User " + user.getUsername());
            return false;
        }
        if(user == null) {
            LOG.warn("Update Concern without Operator! => No DB Log");
        }


        LOG.debug("User " + (user == null ? null : user.getUsername()) + "triggered update of Concern #" + concern.getId());

        // Return false if Name changed and another Concern already has the same Name
        if(!concernDao.getById(concern.getId()).getName().equals(concern.getName()) && nameAlreadyExists(concern.getName())) {
            LOG.info("User " + (user == null ? null : user.getUsername()) + "tried to change Name of Concern #"
                    + concern.getId() + ". Name already used");
            return false;
        }

        // TODO json
        logService.logAuto(user, LogEntryType.CONCERN_UPDATE, concern.getId(), null);
        return concernDao.update(concern);
    }

    public int add(Concern concern, Operator user) {
        if(concern == null)
        {
            LOG.debug("Error on Concern create: given Concern is null. Code -3");
            return -3;
        }
        if(nameAlreadyExists(concern.getName())) {
            LOG.debug("Error on Concern create: Name of Concern already exists. Code -3");
            return -3;
        }
        if(user == null) {
            LOG.warn("Create Concern without Operator! => No DB Log");
        }

        concern.setId(concernDao.add(concern));
        // TODO json
        logService.logAuto(user, LogEntryType.CONCERN_CREATE, concern.getId(), null);
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
        if(concern == null || user == null) {
            LOG.info("Tried to delete Concern with User or Concern == null");
            return false;
        }

        LOG.warn("Concern #" + concern.getId() + " DELETION TRY! User " + user.getUsername());
        // TODO json
        logService.logAuto(user, LogEntryType.CONCERN_REMOVE, concern.getId(), null);

        boolean ret = concernDao.remove(concern);
        if(ret) {
            LOG.warn("Concern #" + concern.getId() + " DELETED! User " + user.getUsername());
        }
        return ret;
    }

    public List<Concern> getAllActive() {

        return concernDao.getAllActive();
    }
}
