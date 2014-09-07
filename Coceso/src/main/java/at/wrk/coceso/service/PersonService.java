package at.wrk.coceso.service;

import at.wrk.coceso.dao.PersonDao;
import at.wrk.coceso.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonDao personDao;

    public Person getById(int id) {
        return personDao.getById(id);
    }

    public List<Person> getAll() {
        return personDao.getAll();
    }

    public List<? extends Person> getAll(boolean asOperator) {
        return asOperator ? personDao.getAllAsOperator() : getAll();
    }

    public boolean update(Person person) {
        return personDao.update(person);
    }

    public int add(Person person) {
        return personDao.add(person);
    }

    public boolean remove(Person person) {
        return personDao.update(person);
    }
}
