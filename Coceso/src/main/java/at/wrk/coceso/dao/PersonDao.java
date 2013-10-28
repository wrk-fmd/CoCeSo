package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.PersonMapper;
import at.wrk.coceso.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

        return jdbc.queryForObject(q, new Object[] {id}, personMapper);
    }

    public List<Person> searchByName(String name) {
        String q = "SELECT * FROM persons WHERE given_name = ? OR sur_name = ?";

        return jdbc.query(q, new String[] {name, name}, personMapper);
    }

    @Override
    @Deprecated
    public List<Person> getAll(int case_id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean update(Person p) {
        if(p == null || p.id <= 0) return false;

        String q = "UPDATE persons SET allowlogin = ?, dnr = ?, contact = ?, given_name = ?," +
                "sur_name = ?, hashedpw = ?, activecase = ? WHERE id = ?";

        jdbc.update(q, p.allowLogin, p.dNr, p.contact, p.given_name, p.sur_name, p.hashedPW,
                p.activeCase == null ? null : p.activeCase.id);

        return true;
    }

    @Override
    public boolean updateFull(Person person) {
        return update(person);
    }

    @Override
    public boolean add(Person p) {
        if(p == null) return false;

        String q = "INSERT INTO persons (allowlogin, dnr, contact, given_name, sur_name, hashedpw, activecase) " +
                "VALUES (?,?,?,?,?,?,?)";

        jdbc.update(q, p.allowLogin, p.dNr, p.contact, p.given_name, p.sur_name, p.hashedPW,
                p.activeCase == null ? null : p.activeCase.id);

        return true;
    }

    @Override
    public boolean remove(Person p) {
        if(p == null) return false;

        String q = "DELETE FROM persons WHERE id = ?";

        jdbc.update(q, p.id);

        return true;
    }
}
