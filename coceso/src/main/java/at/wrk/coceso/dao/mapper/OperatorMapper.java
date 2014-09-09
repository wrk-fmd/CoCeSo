package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.RoleDao;
import at.wrk.coceso.entity.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class OperatorMapper implements RowMapper<Operator> {

    @Autowired
    ConcernDao concernDao;

    @Autowired
    RoleDao roleDao;

    @Override
    public Operator mapRow(ResultSet rs, int i) throws SQLException {
        Operator p = new Operator();

        //Basic Datatypes
        p.setId(rs.getInt("id"));
        p.setAllowLogin(rs.getBoolean("allowLogin"));
        p.setUsername(rs.getString("username"));
        p.setHashedPW(rs.getString("hashedPW"));

        p.setdNr(rs.getInt("dNr"));
        p.setContact(rs.getString("contact"));
        p.setGiven_name(rs.getString("given_name"));
        p.setSur_name(rs.getString("sur_name"));

        p.setInternalAuthorities(roleDao.getByOperatorId(p.getId()));

        // References
        p.setActiveConcern(concernDao.getById(rs.getInt("concern_fk")));

        return p;
    }
}
