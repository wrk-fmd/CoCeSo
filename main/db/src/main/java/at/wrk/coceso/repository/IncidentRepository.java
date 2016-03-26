package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {

  List<Incident> findByIdIn(List<Integer> id);

  List<Incident> findByConcern(Concern concern);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND type != 'Treatment' AND (state != 'Done' OR type NOT IN ('ToHome', 'Standby', 'HoldPosition'))")
  List<Incident> findRelevant(@Param("concern") Concern concern);

  @Query("SELECT i FROM Incident i WHERE concern = :concern AND state != 'Done'")
  List<Incident> findActive(@Param("concern") Concern concern);

  @Query("SELECT i FROM Incident i WHERE i.concern = :concern AND state IN :states")
  List<Incident> findByState(@Param("concern") Concern concern, @Param("states") IncidentState... states);

  @Query("SELECT l.incident.id FROM LogEntry l WHERE l.unit = :unit AND l.incident IS NOT NULL GROUP BY l.incident")
  List<Integer> findRelated(@Param("unit") Unit unit);

  @Query("SELECT i FROM Incident i WHERE "
      + "i.ao.info IN (SELECT u.call FROM Unit u WHERE u.type IN ('Triage', 'Treatment') AND u.concern = :concern) "
      + "AND i.type IN ('Task', 'Transport') AND i.concern = :concern")
  List<Incident> findIncoming(@Param("concern") Concern concern);

  @Query("SELECT i FROM Incident i WHERE i.ao.info = :call AND i.type IN ('Task', 'Transport') AND i.concern = :concern")
  List<Incident> findIncoming(@Param("concern") Concern concern, @Param("call") String call);
}
