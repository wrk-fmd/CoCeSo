package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {

  String NOT_SINGLE_UNIT_INCIDENT = "type NOT IN ('ToHome', 'Standby', 'HoldPosition', 'Treatment')";

  List<Incident> findByIdIn(List<Integer> id);

  List<Incident> findByConcern(Concern concern);

  List<Incident> findByConcern(Concern concern, Sort sort);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND type <> 'Treatment' AND (state <> 'Done' OR type NOT IN ('ToHome', 'Standby', 'HoldPosition'))")
  List<Incident> findRelevant(@Param("concern") Concern concern);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND state <> 'Done'")
  List<Incident> findActive(@Param("concern") Concern concern, Sort sort);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND " + NOT_SINGLE_UNIT_INCIDENT)
  List<Incident> findNonSingleUnit(@Param("concern") Concern concern, Sort sort);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND state <> 'Done' AND " + NOT_SINGLE_UNIT_INCIDENT)
  List<Incident> findActiveNonSingleUnit(@Param("concern") Concern concern, Sort sort);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND type = 'Transport'")
  List<Incident> findTransports(@Param("concern") Concern concern, Sort sort);

  @Query("SELECT l.incident.id FROM LogEntry l WHERE l.unit = :unit AND l.incident IS NOT NULL GROUP BY l.incident")
  List<Integer> findRelated(@Param("unit") Unit unit);

  @Query(nativeQuery = true,
      value = "SELECT * FROM Incident i WHERE i.ao->>'@type' = 'unit' " +
              "AND i.type IN ('Task', 'Transport') AND i.concern_fk = :concern AND i.state <> 'Done'")
  List<Incident> findIncoming(@Param("concern") Concern concern);

  @Query(nativeQuery = true,
      value = "SELECT * FROM Incident i WHERE i.ao->>'@type' = 'unit' AND i.ao->>'id' = CAST(:unit AS TEXT) " +
              "AND i.type IN ('Task', 'Transport') AND i.concern_fk = :concern AND i.state <> 'Done'")
  List<Incident> findIncoming(@Param("concern") Concern concern, @Param("unit") int unit);

  @Query("SELECT COUNT(DISTINCT patient) FROM Incident i WHERE type = 'Treatment' AND patient IS NOT NULL AND concern = :concern")
  long countTreatments(@Param("concern") Concern concern);

  @Query(nativeQuery = true,
      value = "SELECT COUNT(DISTINCT patient_fk) FROM Incident i "
      + " WHERE type = 'Transport' AND bo->>'@type' = 'unit' AND patient_fk IS NOT NULL AND concern_fk = :concern")
  long countTransports(@Param("concern") Concern concern);

}
