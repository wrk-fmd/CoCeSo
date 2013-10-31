package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.CaseDao;
import at.wrk.coceso.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PersonMapper implements RowMapper<Person> {

    @Autowired
    CaseDao caseDao;

    @Override
    public Person mapRow(ResultSet rs, int i) throws SQLException {
        Person p = new Person();

        //Basic Datatypes
        p.id = rs.getInt("id");
        p.allowLogin = rs.getBoolean("allowLogin");
        p.dNr = rs.getInt("dNr");
        p.contact = rs.getString("contact");
        p.given_name = rs.getString("given_name");
        p.sur_name = rs.getString("sur_name");
        p.username = rs.getString("username");
        p.hashedPW = rs.getString("hashedPW");

        // References
        p.activeCase = caseDao.getById(rs.getInt("activeCase"));

        return p;
    }
}
