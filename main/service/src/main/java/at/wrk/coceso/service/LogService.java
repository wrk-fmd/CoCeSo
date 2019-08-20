package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public interface LogService {

  void logAuto(LogEntryType type, Concern concern, Changes changes);

  void logAuto(LogEntryType type, Concern concern, Unit unit, Incident incident, Changes changes);

  void logAuto(LogEntryType type, Concern concern, Unit unit, Incident incident, TaskState state);

  void logAuto(LogEntryType type, Concern concern, Unit unit, Incident incident, TaskState state, Changes changes);

  void logAuto(LogEntryType type, Concern concern, Patient patient, Changes changes);

  void logAuto(LogEntryType type, Concern concern, Incident incident, Patient patient);

  void logCustom(String text, Concern concern, Unit unit, Incident incident);

  List<LogEntry> getAll(Concern concern);

  List<LogEntry> getLast(Concern concern, int count);

  List<LogEntry> getByIncident(Incident incident);

  List<LogEntry> getByIncidentAsc(Incident incident);

  List<LogEntry> getByUnit(Unit unit);

  List<LogEntry> getByUnitAsc(Unit unit);

  List<LogEntry> getByPatient(Patient patient);

  List<LogEntry> getStatesByPatient(Patient patient);

  List<LogEntry> getLimitedByUnit(Unit unit, int count);

  List<LogEntry> getByIncidentAndUnit(Incident incident, Unit unit);

  Timestamp getLastTaskStateUpdate(Incident incident, Unit unit);

  List<LogEntry> getCustom(Concern concern);

  List<LogEntry> getCustomAsc(Concern concern);

  void updateForRemoval(Unit unit);

}
