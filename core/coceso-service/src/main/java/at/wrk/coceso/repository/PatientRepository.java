package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

  List<Patient> findByConcern(Concern concern);

  Page<Patient> findByConcern(Concern concern, Pageable pageable);

  List<Patient> findByConcern(Concern concern, Sort sort);

  List<Patient> findAll(Specification<Patient> spec);

  Page<Patient> findAll(Specification<Patient> spec, Pageable pageable);

  @Query("SELECT p FROM Patient p JOIN p.incidents i WHERE i.closed IS NULL AND i.type = 'Treatment' AND p.concern = :concern ORDER BY p.id DESC")
  List<Patient> findInTreatment(@Param("concern") Concern concern);

}
