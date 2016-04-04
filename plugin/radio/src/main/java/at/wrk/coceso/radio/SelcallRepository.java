package at.wrk.coceso.radio;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelcallRepository extends JpaRepository<Selcall, Integer> {

  public List<Selcall> findByAni(String ani);

  public List<Selcall> findByTimestampGreaterThanAndDirectionIn(OffsetDateTime timestamp, Collection<Selcall.Direction> direction);
}
