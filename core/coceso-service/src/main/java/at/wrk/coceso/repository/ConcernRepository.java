package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcernRepository extends JpaRepository<Concern, Long> {

    Concern findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT c FROM Concern c WHERE c.closed = false")
    List<Concern> findAllOpen();
}
