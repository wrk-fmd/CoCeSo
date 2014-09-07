package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.OperatorMapper;
import at.wrk.coceso.dao.mapper.PersonMapper;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Person;
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
public class PersonDao extends CocesoDao<Person> {

    @Autowired
    PersonMapper personMapper;

    @Autowired
    OperatorMapper operatorMapper;

    @Autowired
    public PersonDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Person getById(int id) {
        if(id <= 0) return null;

        String q = "SELECT * FROM person WHERE id = ?";

        try {
            return jdbc.queryForObject(q, new Object[]{id}, personMapper);
        } catch(DataAccessException e) {
            return null;
        }
    }

    public List<Person> searchByName(String name) {
        String q = "SELECT * FROM person WHERE given_name = ? OR sur_name = ?";

        return jdbc.query(q, new String[] {name, name}, personMapper);
    }

    @Override
    @Deprecated
    public List<Person> getAll(int case_id) {
        throw new UnsupportedOperationException();
    }

    public List<Person> getAll() {
        String q = "SELECT * FROM person";

        return jdbc.query(q, personMapper);
    }

    public List<Operator> getAllAsOperator() {
        String q = "SELECT * FROM person p LEFT JOIN operator o ON (o.id = p.id)";

        return jdbc.query(q, operatorMapper);
    }

    @Override
    public boolean update(Person p) {
        if(p == null || p.getId() <= 0) return false;

        String q = "UPDATE person SET dnr = ?, contact = ?, given_name = ?, " +
                "sur_name = ? WHERE id = ?";

        jdbc.update(q, p.getdNr(), p.getContact(), p.getGiven_name(), p.getSur_name(), p.getId());

        return true;
    }

    @Override
    public int add(final Person p) {
        if(p == null) return -1;

        final String q = "INSERT INTO person (dnr, contact, given_name, " +
                "sur_name) " +
                "VALUES (?,?,?,?)";

        KeyHolder holder = new GeneratedKeyHolder();

        jdbc.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, p.getdNr());
                ps.setString(2, p.getContact() == null ? "" : p.getContact());
                ps.setString(3, p.getGiven_name());
                ps.setString(4, p.getSur_name());

                return ps;
            }
        }, holder);

        return (Integer) holder.getKeys().get("id");
    }

    @Override
    public boolean remove(Person p) {
        if(p == null) return false;

        String q = "DELETE FROM person WHERE id = ?";

        jdbc.update(q, p.getId());

        return true;
    }
}
