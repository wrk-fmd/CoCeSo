package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PersonMapper implements RowMapper<Person> {

    @Autowired
    ConcernDao concernDao;

    @Override
    public Person mapRow(ResultSet rs, int i) throws SQLException {
        Person p = new Person();

        //Basic Datatypes
        p.setId(rs.getInt("id"));
        p.setdNr(rs.getInt("dNr"));
        p.setContact(rs.getString("contact"));
        p.setGiven_name(rs.getString("given_name"));
        p.setSur_name(rs.getString("sur_name"));

        return p;
    }
}
