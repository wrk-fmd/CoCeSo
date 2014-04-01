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

    /**
     * Search case-insensitive for operator by username
     */
    public Operator getByUsername(String username) {
        if(username == null) return null;

        String q = "SELECT * FROM operator o NATURAL JOIN person p WHERE LOWER(o.username) = LOWER(?)";
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
        if(p == null || p.getId() <= 0) return false;

        personDao.update(p);
        String q = "UPDATE operator SET username = ?, allowlogin = ?, hashedpw = ?, concern_fk = ? WHERE id = ?";

        jdbc.update(q, p.getUsername(), p.isAllowLogin(), p.getHashedPW(), p.getActiveConcern() == null ? null : p.getActiveConcern().getId(), p.getId());

        return true;
    }

    @Override
    public int add(final Operator p) {
        if(p == null) return -1;

        Person t = personDao.getById(p.getId());

        if(t == null) {
            p.setId(personDao.add(p));
        }

        final String q = "INSERT INTO operator (id, allowlogin, username, hashedpw, concern_fk) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder holder = new GeneratedKeyHolder();

        jdbc.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, p.getId());
                ps.setBoolean(2, p.isAllowLogin());
                ps.setString(3, p.getUsername());
                ps.setString(4, p.getHashedPW());
                if(p.getActiveConcern() == null)
                    ps.setObject(5, null);
                else ps.setInt(5, p.getActiveConcern().getId());

                return ps;
            }
        }, holder);

        roleDao.add(p.getId(), p.getAuthorities());

        return (Integer) holder.getKeys().get("id");
    }

    @Override
    public boolean remove(Operator p) {
        if(p == null) return false;

        String q = "DELETE FROM operator WHERE id = ?";

        jdbc.update(q, p.getId());

        return true;
    }
}
