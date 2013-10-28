package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.CrewPersonMapper;
import at.wrk.coceso.dao.mapper.CrewUnitMapper;
import at.wrk.coceso.entities.Person;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

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
        List<Integer> keys = jdbc.query("SELECT * FROM crews WHERE units_id = ?", new Object[] {unit_id}, crewPersonMapper);
        List<Person> persons = new ArrayList<Person>();

        for(Integer k : keys) {
            persons.add(personDao.getById(k));
        }

        return persons;
    }


    public boolean remove(Unit unit, Person person) {
        if(unit == null || person == null || unit.id <= 0 || person.id <= 0) return false;

        String q = "DELETE FROM crews WHERE units_id = ? AND persons_id = ?";

        jdbc.update(q, unit.id, person.id);

        return true;
    }


    public boolean add(Unit unit, Person person) {
        if(unit == null || person == null || unit.id <= 0 || person.id <= 0) return false;

        String q = "INSERT INTO crews (units_id, persons_id) VALUES (?,?)";

        jdbc.update(q, unit.id, person.id);

        return true;
    }
}
