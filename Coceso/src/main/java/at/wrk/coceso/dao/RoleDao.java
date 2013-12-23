package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.RoleMapper;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * Created by Robert on 14.12.13.
 *
 */
@Repository
public class RoleDao {

    @Autowired
    RoleMapper roleMapper;

    private JdbcTemplate jdbc;

    @Autowired
    public RoleDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public List<CocesoAuthority> getByOperatorId(int operator_fk) {
        return jdbc.query("SELECT * FROM operator_role WHERE operator_fk = ?",
                new Object[]{operator_fk}, roleMapper);

    }

    public void remove(int operator_fk, CocesoAuthority authority) {
        jdbc.update("DELETE FROM operator_role WHERE operator_fk = ? AND role = ?", operator_fk, authority.name());
    }

    public boolean add(int operator_fk, CocesoAuthority authority) {
        if(operator_fk <= 0 || authority == null){
            Logger.debug("RoleDao: Invalid values: op_fk:"+operator_fk+", authority:"+authority);
            return false;
        }

        String q = "INSERT INTO operator_role (operator_fk, role) VALUES (?,?)";

        try {
            jdbc.update(q, operator_fk, authority.name());
        } catch (DataAccessException dae) {
            Logger.debug("RoleDao.add(): "+dae.getMessage());
        }

        return true;
    }

    public boolean add(int operator_fk, List<CocesoAuthority> authorities) {
        if(authorities == null)
            return false;
        boolean ret = true;
        for(CocesoAuthority auth : authorities) {
            ret &= add(operator_fk, auth);
        }
        return ret;
    }

    public boolean add(int operator_fk, Collection<? extends GrantedAuthority> authorities) {
        if(authorities == null)
            return false;
        boolean ret = true;
        for(GrantedAuthority auth : authorities) {
            ret &= add(operator_fk, CocesoAuthority.valueOf(auth.getAuthority()));
        }
        return ret;
    }
}
