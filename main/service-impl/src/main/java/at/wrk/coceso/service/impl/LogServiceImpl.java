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
import at.wrk.coceso.utils.AuthenticatedUserProvider;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
class LogServiceImpl implements LogService {
    private static final Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);

    private final static Sort TIMESTAMP_DESCENDING = Sort.by(Sort.Direction.DESC, "timestamp");
    private final static Sort TIMESTAMP_ASCENDING = Sort.by(Sort.Direction.ASC, "timestamp");

    private final static Set<LogEntryType> OVERVIEW_ENTRY_TYPE = ImmutableSet.of(
            LogEntryType.UNIT_ASSIGN,
            LogEntryType.UNIT_DETACH,
            LogEntryType.UNIT_AUTO_DETACH,
            LogEntryType.TASKSTATE_CHANGED);

    @Autowired
    private LogRepository logRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    LogServiceImpl(final AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public void logAuto(final LogEntryType type, final Concern concern, final Changes changes) {
        logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, null, null, null, null, changes));
    }

    @Override
    public void logAuto(final LogEntryType type, final Concern concern, final Unit unit, final Incident incident, final Changes changes) {
        logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, unit, incident, null, null, changes));
    }

    @Override
    public void logAuto(final LogEntryType type, final Concern concern, final Unit unit, final Incident incident, final TaskState state) {
        logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, unit, incident, null, state, null));
    }

    @Override
    public void logAuto(final LogEntryType type,
            final Concern concern,
            final Unit unit,
            final Incident incident,
            final TaskState state,
            final Changes changes) {
        logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, unit, incident, null, state, changes));
    }

    @Override
    public void logAuto(final LogEntryType type, final Concern concern, final Patient patient, final Changes changes) {
        logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, null, null, patient, null, changes));
    }

    @Override
    public void logAuto(final LogEntryType type, final Concern concern, final Incident incident, final Patient patient) {
        logRepository.saveAndFlush(new LogEntry(getUser(), type, type.name(), concern, null, incident, patient, null, null));
    }

    @Override
    public void logCustom(final String text, final Concern concern, final Unit unit, final Incident incident) {
        logRepository.saveAndFlush(new LogEntry(getUser(), LogEntryType.CUSTOM, text, concern, unit, incident, null, null, null));
    }

    @Override
    public List<LogEntry> getAll(final Concern concern) {
        return logRepository.findByConcern(concern, TIMESTAMP_DESCENDING);
    }

    @Override
    public List<LogEntry> getLast(final Concern concern, final int count) {
        return logRepository.findByConcern(concern, PageRequest.of(0, count, TIMESTAMP_DESCENDING));
    }

    @Override
    public List<LogEntry> getByIncident(final Incident incident) {
        return logRepository.findByIncident(incident, TIMESTAMP_DESCENDING);
    }

    @Override
    public List<LogEntry> getByIncidentAsc(final Incident incident) {
        return logRepository.findByIncident(incident, TIMESTAMP_ASCENDING);
    }

    @Override
    public List<LogEntry> getByUnit(final Unit unit) {
        return logRepository.findByUnit(unit, TIMESTAMP_DESCENDING);
    }

    @Override
    public List<LogEntry> getByUnitAsc(final Unit unit) {
        return logRepository.findByUnit(unit, TIMESTAMP_ASCENDING);
    }

    @Override
    public List<LogEntry> getByPatient(final Patient patient) {
        return getLogEntriesForPatient(patient, entry -> true);
    }

    @Override
    public List<LogEntry> getPatientLogsFilteredByOverviewStates(final Patient patient) {
        return getLogEntriesForPatient(patient, entry -> OVERVIEW_ENTRY_TYPE.contains(entry.getType()));
    }

    @Override
    public List<LogEntry> getLimitedByUnit(final Unit unit, final int count) {
        return logRepository.findByUnit(unit, PageRequest.of(0, count, TIMESTAMP_DESCENDING));
    }

    @Override
    public List<LogEntry> getByIncidentAndUnit(final Incident incident, final Unit unit) {
        return logRepository.findByIncidentAndUnit(incident, unit, TIMESTAMP_DESCENDING);
    }

    @Override
    public Timestamp getLastTaskStateUpdate(final Incident incident, final Unit unit) {
        List<LogEntry> last = logRepository.findLast(PageRequest.of(0, 1), incident, unit,
                LogEntryType.TASKSTATE_CHANGED, LogEntryType.UNIT_ASSIGN, LogEntryType.UNIT_DETACH, LogEntryType.UNIT_AUTO_DETACH);
        return last.isEmpty() ? null : last.get(0).getTimestamp();
    }

    @Override
    public List<LogEntry> getCustom(final Concern concern) {
        return logRepository.findByConcernAndType(concern, LogEntryType.CUSTOM, TIMESTAMP_DESCENDING);
    }

    @Override
    public List<LogEntry> getCustomAsc(final Concern concern) {
        return logRepository.findByConcernAndType(concern, LogEntryType.CUSTOM, TIMESTAMP_ASCENDING);
    }

    @Override
    public void updateForRemoval(final Unit unit) {
        logRepository.updateForRemoval(unit);
    }


    private List<LogEntry> getLogEntriesForPatient(final Patient patient, final Predicate<LogEntry> logEntryFilter) {
        List<LogEntry> logEntries = new LinkedList<>(logRepository.findByPatient(patient, TIMESTAMP_DESCENDING));
        if (patient.getIncidents() != null && !patient.getIncidents().isEmpty()) {
            logEntries.addAll(logRepository.findByIncidentList(patient.getIncidents(), TIMESTAMP_DESCENDING));
        }

        return logEntries
                .stream()
                .filter(logEntryFilter)
                .sorted(Comparator.comparing(LogEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    private User getUser() {
        AuthenticatedUser authUser = authenticatedUserProvider.getAuthenticatedUser();
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
