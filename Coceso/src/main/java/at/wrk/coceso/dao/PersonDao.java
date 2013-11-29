package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.PersonMapper;
import at.wrk.coceso.entities.Person;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

        String q = "SELECT * FROM persons WHERE id = ?";

        try {
            return jdbc.queryForObject(q, new Object[]{id}, personMapper);
        } catch(DataAccessException e) {
            return null;
        }
    }

    public Person getByUsername(String username) {
        if(username == null) return null;

        String q = "SELECT * FROM persons WHERE username = ?";
        try {
            return jdbc.queryForObject(q, new Object[] {username}, personMapper);
        } catch(DataAccessException e) {
            Logger.debug("PersonDAO.getByUsername "+e.getMessage());
            return null;
        }
    }

    public List<Person> searchByName(String name) {
        String q = "SELECT * FROM persons WHERE given_name = ? OR sur_name = ?";

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

        String q = "UPDATE persons SET allowlogin = ?, dnr = ?, contact = ?, given_name = ?," +
                "sur_name = ?, hashedpw = ?, activecase = ? WHERE id = ?";

        jdbc.update(q, p.allowLogin, p.dNr, p.contact, p.given_name, p.sur_name, p.hashedPW,
                p.activeCase == null ? null : p.activeCase.id, p.id);

        return true;
    }

    @Override
    public int add(Person p) {
        if(p == null) return -1;

        String q = "INSERT INTO persons (allowlogin, dnr, contact, given_name, " +
                "sur_name, username, hashedpw, activecase) " +
                "VALUES (?,?,?,?,?,?,?)";

        jdbc.update(q, p.allowLogin, p.dNr, p.contact, p.given_name, p.sur_name, p.username, p.hashedPW,
                p.activeCase == null ? null : p.activeCase.id);

        return 0;
    }

    @Override
    public boolean remove(Person p) {
        if(p == null) return false;

        String q = "DELETE FROM persons WHERE id = ?";

        jdbc.update(q, p.id);

        return true;
    }
}
