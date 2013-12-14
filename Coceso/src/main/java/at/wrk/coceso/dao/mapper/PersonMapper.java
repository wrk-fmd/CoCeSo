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
        p.id = rs.getInt("id");
        p.dNr = rs.getInt("dNr");
        p.contact = rs.getString("contact");
        p.given_name = rs.getString("given_name");
        p.sur_name = rs.getString("sur_name");

        return p;
    }
}
