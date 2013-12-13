package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.PersonMapper;
import at.wrk.coceso.entities.Person;
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

    @Override
    public boolean update(Person p) {
        if(p == null || p.id <= 0) return false;

        String q = "UPDATE person SET dnr = ?, contact = ?, given_name = ?, " +
                "sur_name = ? WHERE id = ?";

        jdbc.update(q,  p.dNr, p.contact, p.given_name, p.sur_name, p.id);

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

                ps.setInt(1, p.dNr);
                ps.setString(2, p.contact);
                ps.setString(3, p.given_name);
                ps.setString(4, p.sur_name);

                return ps;
            }
        }, holder);

        return (Integer) holder.getKeys().get("id");
    }

    @Override
    public boolean remove(Person p) {
        if(p == null) return false;

        String q = "DELETE FROM person WHERE id = ?";

        jdbc.update(q, p.id);

        return true;
    }
}
