package at.wrk.coceso.radio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SelcallRepository extends JpaRepository<Selcall, Integer> {

    List<Selcall> findByTimestampGreaterThanAndDirectionIn(final OffsetDateTime timestamp, final Collection<Selcall.Direction> direction);
}
