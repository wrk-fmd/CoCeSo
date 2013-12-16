package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.OperatorMapper;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@Repository
public class OperatorDao extends CocesoDao<Operator> {

    @Autowired
    private OperatorMapper operatorMapper;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    public OperatorDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Operator getById(int id) {
        if(id <= 0) return null;

        String q = "SELECT * FROM operator o NATURAL JOIN person p WHERE p.id = ?";

        try {
            return jdbc.queryForObject(q, new Object[]{id}, operatorMapper);
        } catch(DataAccessException e) {
            return null;
        }
    }

    public Operator getByUsername(String username) {
        if(username == null) return null;

        String q = "SELECT * FROM operator o NATURAL JOIN person p WHERE o.username = ?";
        try {
            return jdbc.queryForObject(q, new Object[] {username}, operatorMapper);
        } catch(DataAccessException e) {
            Logger.debug("PersonDAO.getByUsername "+e.getMessage());
            return null;
        }
    }

    public List<Operator> searchByName(String name) {
        String q = "SELECT * FROM operator o NATURAL JOIN person p  " +
                "WHERE p.given_name = ? OR p.sur_name = ?";

        return jdbc.query(q, new String[] {name, name}, operatorMapper);
    }

    @Override
    @Deprecated
    public List<Operator> getAll(int case_id) {
        throw new UnsupportedOperationException();
    }

    public List<Operator> getAll() {
        String q = "SELECT * FROM operator o NATURAL JOIN person p";

        return jdbc.query(q, operatorMapper);
    }

    @Override
    public boolean update(Operator p) {
        if(p == null || p.id <= 0) return false;

        personDao.update(p);
        String q = "UPDATE operator SET allowlogin = ?, hashedpw = ?, concern_fk = ? WHERE id = ?";

        jdbc.update(q, p.allowLogin, p.hashedPW, p.activeConcern == null ? null : p.activeConcern.id, p.id);

        return true;
    }

    @Override
    public int add(final Operator p) {
        if(p == null) return -1;

        Person t = personDao.getById(p.id);

        if(t == null) {
            p.id = personDao.add(p);
        }

        final String q = "INSERT INTO operator (id, allowlogin, username, hashedpw, concern_fk) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder holder = new GeneratedKeyHolder();

        jdbc.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, p.id);
                ps.setBoolean(2, p.allowLogin);
                ps.setString(3, p.username);
                ps.setString(4, p.hashedPW);
                if(p.activeConcern == null)
                    ps.setObject(5, null);
                else ps.setInt(5, p.activeConcern.id);

                return ps;
            }
        }, holder);

        roleDao.add(p.id, p.getAuthorities());

        return (Integer) holder.getKeys().get("id");
    }

    @Override
    public boolean remove(Operator p) {
        if(p == null) return false;

        String q = "DELETE FROM operator WHERE id = ?";

        jdbc.update(q, p.id);

        return true;
    }
}
