package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Integer> {

  List<Container> findByConcern(Concern concern);

  @Query("SELECT c FROM Container c WHERE c.concern = :concern AND c.parent IS NULL")
  Container findRootByConcern(@Param("concern") Concern concern);

}
