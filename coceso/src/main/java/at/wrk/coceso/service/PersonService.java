package at.wrk.coceso.service;

import at.wrk.coceso.dao.PersonDao;
import at.wrk.coceso.entity.Person;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PersonService {

    private static final
    Logger LOG = Logger.getLogger(PersonService.class);

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

    public void batchCreate(Set<Person> persons) {
        List<Person> currentPersons = getAll();

        for(Person person : persons) {
            if(currentPersons.contains(person)) {
                LOG.debug(String.format("Person '%s %s' already exists.",
                        person.getGiven_name(), person.getSur_name()));
            } else {
                add(person);
                LOG.debug(String.format("create Person '%s %s' via csv batch",
                        person.getGiven_name(), person.getSur_name()));
            }
        }
    }
}
