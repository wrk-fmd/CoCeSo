package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.JournalEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.JournalEntryType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {

  List<JournalEntry> findByConcern(Concern concern, Pageable pageable);

  List<JournalEntry> findByIncident(Incident incident, Pageable pageable);

  List<JournalEntry> findByUnit(Unit unit, Pageable pageable);

  List<JournalEntry> findByConcern(Concern concern, Sort sort);

  List<JournalEntry> findByIncident(Incident incident, Sort sort);

  List<JournalEntry> findByUnit(Unit unit, Sort sort);

  @Query("SELECT l FROM JournalEntry l WHERE l.patient = :patient OR l.incident IN :incidents")
  List<JournalEntry> findByPatient(@Param("patient") Patient patient, @Param("incidents") Collection<Incident> incidents, Sort sort);

  @Query("SELECT l FROM JournalEntry l WHERE (l.patient = :patient OR l.incident IN :incidents) "
      + "AND l.type IN ('UNIT_ASSIGN', 'UNIT_DETACH', 'UNIT_AUTO_DETACH', 'TASKSTATE_CHANGED')")
  List<JournalEntry> findStatesByPatient(@Param("patient") Patient patient, @Param("incidents") Collection<Incident> incidents, Sort sort);

  List<JournalEntry> findByIncidentAndUnit(Incident incident, Unit unit, Sort sort);

  @Query("SELECT l FROM JournalEntry l WHERE l.incident = :incident AND l.unit = :unit AND l.type IN :types "
      + "ORDER BY l.timestamp DESC")
  List<JournalEntry> findLast(Pageable pageable, @Param("incident") Incident incident, @Param("unit") Unit unit, @Param("types") JournalEntryType... types);

  List<JournalEntry> findByConcernAndType(Concern concern, JournalEntryType type, Sort sort);

  @Modifying
  @Transactional
  @Query("UPDATE JournalEntry SET unit = NULL, text = 'Unit created - REMOVED', type = 'UNIT_CREATE_REMOVED' "
      + "WHERE type = 'UNIT_CREATE' AND unit = :unit")
  void updateForRemoval(@Param("unit") Unit unit);
}
