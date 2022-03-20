package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.LogEntryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Integer> {

  List<LogEntry> findByConcern(Concern concern, Pageable pageable);

  List<LogEntry> findByIncident(Incident incident, Pageable pageable);

  List<LogEntry> findByUnit(Unit unit, Pageable pageable);

  List<LogEntry> findByConcern(Concern concern, Sort sort);

  List<LogEntry> findByIncident(Incident incident, Sort sort);

  List<LogEntry> findByUnit(Unit unit, Sort sort);

  @Query("SELECT l FROM LogEntry l WHERE l.patient = :patient OR l.incident IN :incidents")
  List<LogEntry> findByPatient(@Param("patient") Patient patient, @Param("incidents") Collection<Incident> incidents, Sort sort);

  @Query("SELECT l FROM LogEntry l WHERE (l.patient = :patient OR l.incident IN :incidents) "
      + "AND l.type IN ('UNIT_ASSIGN', 'UNIT_DETACH', 'UNIT_AUTO_DETACH', 'TASKSTATE_CHANGED')")
  List<LogEntry> findStatesByPatient(@Param("patient") Patient patient, @Param("incidents") Collection<Incident> incidents, Sort sort);

  List<LogEntry> findByIncidentAndUnit(Incident incident, Unit unit, Sort sort);

  @Query("SELECT l FROM LogEntry l WHERE l.incident = :incident AND l.unit = :unit AND l.type IN :types "
      + "ORDER BY l.timestamp DESC")
  List<LogEntry> findLast(Pageable pageable, @Param("incident") Incident incident, @Param("unit") Unit unit, @Param("types") LogEntryType... types);

  List<LogEntry> findByConcernAndType(Concern concern, LogEntryType type, Sort sort);

  @Modifying
  @Transactional
  @Query("UPDATE LogEntry SET unit = NULL, text = 'Unit created - REMOVED', type = 'UNIT_CREATE_REMOVED' "
      + "WHERE type = 'UNIT_CREATE' AND unit = :unit")
  void updateForRemoval(@Param("unit") Unit unit);
}
