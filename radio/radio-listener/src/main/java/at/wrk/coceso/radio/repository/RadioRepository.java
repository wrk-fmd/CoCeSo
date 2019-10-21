package at.wrk.coceso.radio.repository;

import at.wrk.coceso.radio.entity.RadioCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface RadioRepository extends JpaRepository<RadioCall, Integer> {

  List<RadioCall> findByAni(String ani);

  List<RadioCall> findByTimestampGreaterThanAndDirectionIn(OffsetDateTime timestamp, Collection<RadioCall.Direction> direction);
}
