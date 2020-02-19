package at.wrk.coceso.service.impl;

import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.repository.LogRepository;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.AuthenicatedUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
class LogServiceImpl implements LogService {
  private static final Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);

  private final static Sort sortDesc = new Sort(Sort.Direction.DESC, "timestamp");
  private final static Sort sortAsc = new Sort(Sort.Direction.ASC, "timestamp");

  @Autowired
  private LogRepository logRepository;

  private final AuthenicatedUserProvider authenicatedUserProvider;

  @Autowired
  LogServiceImpl(final AuthenicatedUserProvider authenicatedUserProvider) {
    this.authenicatedUserProvider = authenicatedUserProvider;
  }

  @Override
  public void logAuto(LogEntryType type, Concern concern, Changes changes) {
    logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, null, null, null, null, changes));
  }

  @Override
  public void logAuto(LogEntryType type, Concern concern, Unit unit, Incident incident, Changes changes) {
    logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, unit, incident, null, null, changes));
  }

  @Override
  public void logAuto(LogEntryType type, Concern concern, Unit unit, Incident incident, TaskState state) {
    logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, unit, incident, null, state, null));
  }

  @Override
  public void logAuto(LogEntryType type, Concern concern, Unit unit, Incident incident, TaskState state, Changes changes) {
    logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, unit, incident, null, state, changes));
  }

  @Override
  public void logAuto(LogEntryType type, Concern concern, Patient patient, Changes changes) {
    logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, null, null, patient, null, changes));
  }

  @Override
  public void logAuto(LogEntryType type, Concern concern, Incident incident, Patient patient) {
    logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, null, incident, patient, null, null));
  }

  @Override
  public void logCustom(String text, Concern concern, Unit unit, Incident incident) {
    logRepository.saveAndFlush(new LogEntry(getUser(), LogEntryType.CUSTOM, text, concern, unit, incident, null, null, null));
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
  public List<LogEntry> getByPatient(Patient patient) {
    return logRepository.findByPatient(patient, patient.getIncidents(), sortDesc);
  }

  @Override
  public List<LogEntry> getStatesByPatient(Patient patient) {
    return logRepository.findStatesByPatient(patient, patient.getIncidents(), sortDesc);
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


  private User getUser() {
    AuthenticatedUser authUser = authenicatedUserProvider.getAuthenticatedUser();
    User user;
    if (authUser != null) {
      user = new User();
      user.setId(authUser.getUserId());
    } else {
      LOG.trace("Writing log entry to database without authenticated user.");
      user = null;
    }

    return user;
  }
}
