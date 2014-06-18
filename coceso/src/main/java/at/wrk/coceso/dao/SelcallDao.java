package at.wrk.coceso.dao;

import at.wrk.coceso.entity.Selcall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Robert on 14.06.2014.
 */
@Repository
public interface SelcallDao extends JpaRepository<Selcall, Integer> {
    public List<Selcall> findByAni(String ani);

    //@Query("SELECT s FROM selcall s WHERE timestamp + '1 hour'::INTERVAL > (CURRENT_TIMESTAMP  )")
    //public List<Selcall> getLastHour();
}
