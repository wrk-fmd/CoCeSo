package at.wrk.coceso.dao;

import at.wrk.coceso.entity.Selcall;
import java.util.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Robert on 14.06.2014.
 */
@Repository
public interface SelcallDao extends JpaRepository<Selcall, Integer> {
    public List<Selcall> findByAni(String ani);

    public List<Selcall> findByTimestampGreaterThanAndDirection(Calendar timestamp, Selcall.Direction direction);
}
