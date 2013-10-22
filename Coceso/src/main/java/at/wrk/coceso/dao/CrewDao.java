package at.wrk.coceso.dao;


import at.wrk.coceso.entities.Person;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CrewDao {

    private JdbcTemplate jdbc;

    @Autowired
    public CrewDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }


    public List<Person> getByUnitId(int unit_id) {
        return null;
    }


    public boolean remove(Unit unit, Person person) {
        return false;
    }


    public boolean add(Unit unit, Person person) {
        return false;
    }
}
