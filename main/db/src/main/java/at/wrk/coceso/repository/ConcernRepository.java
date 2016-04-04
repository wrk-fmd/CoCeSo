package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcernRepository extends JpaRepository<Concern, Integer> {

  public Concern findByName(String name);

}
