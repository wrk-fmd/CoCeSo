package at.wrk.coceso.service;

import at.wrk.coceso.dao.OperatorDao;
import at.wrk.coceso.entity.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorService {

    @Autowired
    private OperatorDao operatorDao;

    public Operator getById(int id) {
        return operatorDao.getById(id);
    }

    public List<Operator> getAll() {
        return operatorDao.getAll();
    }

    public boolean update(Operator operator) {
        return operatorDao.update(operator);
    }

    public int add(Operator operator) {
        return operatorDao.add(operator);
    }

    public boolean remove(Operator operator) {
        return operatorDao.update(operator);
    }
}
