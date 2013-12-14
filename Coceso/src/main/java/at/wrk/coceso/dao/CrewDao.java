package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.CrewPersonMapper;
import at.wrk.coceso.dao.mapper.CrewUnitMapper;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.entity.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

// TODO CHECK!!

@Repository
public class CrewDao {

    @Autowired
    CrewPersonMapper crewPersonMapper;

    @Autowired
    CrewUnitMapper crewUnitMapper;

    @Autowired
    PersonDao personDao;

    private JdbcTemplate jdbc;

    @Autowired
    public CrewDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }


    public List<Person> getByUnitId(int unit_id) {
        List<Integer> keys = jdbc.query("SELECT * FROM crew WHERE unit_fk = ?", new Object[] {unit_id}, crewPersonMapper);
        List<Person> persons = new ArrayList<Person>();

        for(Integer k : keys) {
            persons.add(personDao.getById(k));
        }

        return persons;
    }


    public boolean remove(Unit unit, Person person) {
        if(unit == null || person == null || unit.id <= 0 || person.id <= 0) return false;

        String q = "DELETE FROM crew WHERE unit_fk = ? AND person_fk = ?";

        jdbc.update(q, unit.id, person.id);

        return true;
    }


    public boolean add(Unit unit, Person person) {
        if(unit == null || person == null || unit.id <= 0 || person.id <= 0) return false;

        String q = "INSERT INTO crew (unit_fk, person_fk) VALUES (?,?)";

        jdbc.update(q, unit.id, person.id);

        return true;
    }
}
