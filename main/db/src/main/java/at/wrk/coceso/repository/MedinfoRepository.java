package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MedinfoRepository extends JpaRepository<Medinfo, Integer> {

  public List<Medinfo> findByConcern(Concern concern);

  public Page<Medinfo> findByConcern(Concern concern, Pageable pageable);

  public List<Medinfo> findAll(Specification<Medinfo> spec);

  @Transactional
  public int deleteByConcern(Concern concern);

}
