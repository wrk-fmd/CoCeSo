package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

  public List<Patient> findByConcern(Concern concern);

  public Page<Patient> findByConcern(Concern concern, Pageable pageable);

  public List<Patient> findAll(Specification<Patient> spec);

  public Page<Patient> findAll(Specification<Patient> spec, Pageable pageable);

  @Query("SELECT p FROM Patient p JOIN p.incidents i WHERE i.state != 'Done' AND i.type = 'Treatment' AND p.concern = :concern")
  public List<Patient> findInTreatment(@Param("concern") Concern concern);

  @Transactional
  @Modifying
  @Query("UPDATE Patient SET medinfo = NULL WHERE concern = :concern")
  public int removeMedinfos(@Param("concern") Concern concern);
}
