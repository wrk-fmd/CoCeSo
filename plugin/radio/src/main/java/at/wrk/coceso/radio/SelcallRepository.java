package at.wrk.coceso.radio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SelcallRepository extends JpaRepository<Selcall, Integer> {

  List<Selcall> findByAni(String ani);

  List<Selcall> findByTimestampGreaterThanAndDirectionIn(OffsetDateTime timestamp, Collection<Selcall.Direction> direction);
}
