package at.wrk.coceso.radio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface SelcallRepository extends JpaRepository<Selcall, Integer> {

  List<Selcall> findByAni(String ani);

  List<Selcall> findByTimestampGreaterThanAndDirectionIn(Instant timestamp, Collection<Selcall.Direction> direction);
}
