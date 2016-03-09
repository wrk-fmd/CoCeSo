package at.wrk.coceso.repository;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.LogEntryType;
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
public interface LogRepository extends JpaRepository<LogEntry, Integer> {

  public List<LogEntry> findByConcern(Concern concern, Pageable pageable);

  public List<LogEntry> findByIncident(Incident incident, Pageable pageable);

  public List<LogEntry> findByUnit(Unit unit, Pageable pageable);

  public List<LogEntry> findByConcern(Concern concern, Sort sort);

  public List<LogEntry> findByIncident(Incident incident, Sort sort);

  public List<LogEntry> findByUnit(Unit unit, Sort sort);

  public List<LogEntry> findByIncidentAndUnit(Incident incident, Unit unit, Sort sort);

  @Query("SELECT l FROM LogEntry l WHERE l.incident = :incident AND l.unit = :unit AND l.type IN :types "
          + "ORDER BY l.timestamp DESC")
  public LogEntry findLast(@Param("incident") Incident incident, @Param("unit") Unit unit, @Param("types") LogEntryType... types);

  public List<LogEntry> findByConcernAndType(Concern concern, LogEntryType type, Sort sort);

  @Modifying
  @Transactional
  @Query("UPDATE LogEntry SET unit = NULL, text = 'Unit created - REMOVED', type = 'UNIT_CREATE_REMOVED' "
          + "WHERE type = 'UNIT_CREATE' AND unit = :unit")
  public void updateForRemoval(@Param("unit") Unit unit);
}
