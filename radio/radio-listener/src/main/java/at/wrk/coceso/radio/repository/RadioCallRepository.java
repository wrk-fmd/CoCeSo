package at.wrk.coceso.radio.repository;

import at.wrk.coceso.radio.entity.RadioCall;
import at.wrk.coceso.radio.entity.RadioCall.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface RadioCallRepository extends JpaRepository<RadioCall, Integer> {

    @Query("SELECT r FROM RadioCall r WHERE timestamp >= ?1 AND direction IN ?2 ORDER BY timestamp ASC")
    List<RadioCall> findReceivedAfter(Instant timestamp, Collection<Direction> directions);
}
