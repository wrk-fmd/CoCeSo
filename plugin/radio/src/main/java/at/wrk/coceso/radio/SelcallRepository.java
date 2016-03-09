package at.wrk.coceso.radio;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelcallRepository extends JpaRepository<Selcall, Integer> {
    public List<Selcall> findByAni(String ani);

    public List<Selcall> findByTimestampGreaterThanAndDirectionIn(Calendar timestamp, Collection<Selcall.Direction> direction);
}
