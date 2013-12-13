package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entities.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class OperatorMapper implements RowMapper<Operator> {

    @Autowired
    ConcernDao concernDao;

    @Override
    public Operator mapRow(ResultSet rs, int i) throws SQLException {
        Operator p = new Operator();

        //Basic Datatypes
        p.id = rs.getInt("id");
        p.allowLogin = rs.getBoolean("allowLogin");
        p.username = rs.getString("username");
        p.hashedPW = rs.getString("hashedPW");

        p.dNr = rs.getInt("dNr");
        p.contact = rs.getString("contact");
        p.given_name = rs.getString("given_name");
        p.sur_name = rs.getString("sur_name");


        // References
        p.activeConcern = concernDao.getById(rs.getInt("concern_fk"));

        return p;
    }
}
