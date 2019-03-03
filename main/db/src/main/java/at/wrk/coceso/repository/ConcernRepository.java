package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ConcernRepository extends JpaRepository<Concern, Integer> {

  Concern findByName(String name);

  @Query("SELECT c FROM Concern c WHERE c.closed = false")
  Collection<Concern> findAllOpen();
}
