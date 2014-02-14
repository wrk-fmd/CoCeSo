package at.wrk.coceso.service;


import at.wrk.coceso.dao.CocesoDao;
import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Concern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcernService {

    @Autowired
    private ConcernDao concernDao;

    public Concern getById(int id) {
        return concernDao.getById(id);
    }

    public List<Concern> getAll() {
        return concernDao.getAll();
    }

    public boolean update(Concern concern) {
        return concernDao.update(concern);
    }

    public int add(Concern concern) {
        return concernDao.add(concern);
    }

    public boolean remove(Concern concern) {
        return concernDao.remove(concern);
    }
}
