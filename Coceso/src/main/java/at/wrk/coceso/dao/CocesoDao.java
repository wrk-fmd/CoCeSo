package at.wrk.coceso.dao;

import at.wrk.coceso.entities.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public abstract class CocesoDao<E> {

    protected JdbcTemplate jdbc;

    @Autowired
    public CocesoDao(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    public abstract E getById(int id);

    public abstract List<E> getAll(int case_id);

    public abstract boolean update(E e);

    public abstract boolean add(E e);
}
