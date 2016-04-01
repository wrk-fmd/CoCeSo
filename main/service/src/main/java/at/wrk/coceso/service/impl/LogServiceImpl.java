package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.repository.LogRepository;
import at.wrk.coceso.service.LogService;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class LogServiceImpl implements LogService {

  private final static Sort sortDesc = new Sort(Sort.Direction.DESC, "timestamp");
  private final static Sort sortAsc = new Sort(Sort.Direction.ASC, "timestamp");

  @Autowired
  private LogRepository logRepository;

  @Override
  public void logAuto(User user, LogEntryType type, Concern concern, Changes changes) {
    logRepository.save(new LogEntry(user, type, type.name(), concern, null, null, null, null, changes));
  }

  @Override
  public void logAuto(User user, LogEntryType type, Concern concern, Unit unit, Incident incident, Changes changes) {
    logRepository.save(new LogEntry(user, type, type.name(), concern, unit, incident, null, null, changes));
  }

  @Override
  public void logAuto(User user, LogEntryType type, Concern concern, Unit unit, Incident incident, TaskState state) {
    logRepository.save(new LogEntry(user, type, type.name(), concern, unit, incident, null, state, null));
  }

  @Override
  public void logAuto(User user, LogEntryType type, Concern concern, Unit unit, Incident incident, TaskState state, Changes changes) {
    logRepository.save(new LogEntry(user, type, type.name(), concern, unit, incident, null, state, changes));
  }

  @Override
  public void logAuto(User user, LogEntryType type, Concern concern, Patient patient, Changes changes) {
    logRepository.save(new LogEntry(user, type, type.name(), concern, null, null, patient, null, changes));
  }

  @Override
  public void logAuto(User user, LogEntryType type, Concern concern, Incident incident, Patient patient) {
    logRepository.save(new LogEntry(user, type, type.name(), concern, null, incident, patient, null, null));
  }

  @Override
  public void logCustom(User user, String text, Concern concern, Unit unit, Incident incident) {
    logRepository.save(new LogEntry(user, LogEntryType.CUSTOM, text, concern, unit, incident, null, null, null));
  }

  @Override
  public List<LogEntry> getAll(Concern concern) {
    return logRepository.findByConcern(concern, sortDesc);
  }

  @Override
  public List<LogEntry> getLast(Concern concern, int count) {
    return logRepository.findByConcern(concern, new PageRequest(0, count, sortDesc));
  }

  @Override
  public List<LogEntry> getByIncident(Incident incident) {
    return logRepository.findByIncident(incident, sortDesc);
  }

  @Override
  public List<LogEntry> getByIncidentAsc(Incident incident) {
    return logRepository.findByIncident(incident, sortAsc);
  }

  @Override
  public List<LogEntry> getByUnit(Unit unit) {
    return logRepository.findByUnit(unit, sortDesc);
  }

  @Override
  public List<LogEntry> getByUnitAsc(Unit unit) {
    return logRepository.findByUnit(unit, sortAsc);
  }

  @Override
  public List<LogEntry> getLimitedByUnit(Unit unit, int count) {
    return logRepository.findByUnit(unit, new PageRequest(0, count, sortDesc));
  }

  @Override
  public List<LogEntry> getByIncidentAndUnit(Incident incident, Unit unit) {
    return logRepository.findByIncidentAndUnit(incident, unit, sortDesc);
  }

  @Override
  public Timestamp getLastTaskStateUpdate(Incident incident, Unit unit) {
    List<LogEntry> last = logRepository.findLast(new PageRequest(0, 1), incident, unit,
        LogEntryType.TASKSTATE_CHANGED, LogEntryType.UNIT_ASSIGN, LogEntryType.UNIT_DETACH, LogEntryType.UNIT_AUTO_DETACH);
    return last.isEmpty() ? null : last.get(0).getTimestamp();
  }

  @Override
  public List<LogEntry> getCustom(Concern concern) {
    return logRepository.findByConcernAndType(concern, LogEntryType.CUSTOM, sortDesc);
  }

  @Override
  public List<LogEntry> getCustomAsc(Concern concern) {
    return logRepository.findByConcernAndType(concern, LogEntryType.CUSTOM, sortAsc);
  }

  @Override
  public void updateForRemoval(Unit unit) {
    logRepository.updateForRemoval(unit);
  }

}
