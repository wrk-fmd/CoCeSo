package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.journal.CustomJournalEntryDto;
import at.wrk.coceso.dto.journal.JournalEntryDto;
import at.wrk.coceso.endpoint.ParamValidator;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.JournalEntry;
import at.wrk.coceso.entity.JournalEntry.JournalEntryBuilder;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.journal.ChangesCollector;
import at.wrk.coceso.mapper.JournalMapper;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.repository.JournalRepository;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.utils.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@Transactional
class JournalServiceImpl implements JournalService {

    private final static Sort sortDesc = Sort.by(Sort.Direction.DESC, "timestamp");
    private final static Sort sortAsc = Sort.by(Sort.Direction.ASC, "timestamp");

    private final JournalRepository journalRepository;
    private final IncidentRepository incidentRepository;
    private final UnitRepository unitRepository;
    private final JournalMapper journalMapper;

    @Autowired
    JournalServiceImpl(final JournalRepository journalRepository, final IncidentRepository incidentRepository,
            final UnitRepository unitRepository, JournalMapper journalMapper) {
        this.journalRepository = journalRepository;
        this.incidentRepository = incidentRepository;
        this.unitRepository = unitRepository;
        this.journalMapper = journalMapper;
    }

    @Override
    public void logConcern(JournalEntryType type, Concern concern, ChangesCollector changes) {
        JournalEntry entry = entry(type, concern, changes).build();
        journalRepository.save(entry);
    }

    @Override
    public void logIncident(JournalEntryType type, Incident incident, ChangesCollector changes) {
        JournalEntry entry = entry(type, incident.getConcern(), changes)
                .incident(incident)
                .build();
        journalRepository.save(entry);
    }

    @Override
    public void logUnit(JournalEntryType type, Unit unit, ChangesCollector changes) {
        JournalEntry entry = entry(type, unit.getConcern(), changes)
                .unit(unit)
                .build();
        journalRepository.save(entry);
    }

    @Override
    public void logTask(JournalEntryType type, Task task) {
        JournalEntry entry = entry(type, task.getIncident().getConcern(), null)
                .incident(task.getIncident())
                .unit(task.getUnit())
                .state(task.getState())
                .build();
        journalRepository.save(entry);
    }

    @Override
    public void logTaskDetach(JournalEntryType type, Task task) {
        JournalEntry entry = entry(type, task.getIncident().getConcern(), null)
                .incident(task.getIncident())
                .unit(task.getUnit())
                .state(TaskState.Detached)
                .build();
        journalRepository.save(entry);
    }

    @Override
    public void logPatient(JournalEntryType type, Patient patient, ChangesCollector changes) {
        JournalEntry entry = entry(type, patient.getConcern(), changes)
                .patient(patient)
                .build();
        journalRepository.save(entry);
    }

    @Override
    public void logPatientAssign(JournalEntryType type, Incident incident) {
        JournalEntry entry = entry(type, incident.getConcern(), null)
                .incident(incident)
                .patient(incident.getPatient())
                .build();
        journalRepository.save(entry);
    }

    @Override
    public void logCustom(Concern concern, CustomJournalEntryDto data) {
        JournalEntryBuilder entry = entry(JournalEntryType.CUSTOM, concern, null)
                .text(data.getText());

        if (data.getIncident() != null) {
            Incident incident = incidentRepository.getById(data.getIncident());
            ParamValidator.matches(concern, incident);
            entry.incident(incident);
        }

        if (data.getUnit() != null) {
            Unit unit = unitRepository.getById(data.getUnit());
            ParamValidator.matches(concern, unit);
            entry.unit(unit);
        }

        journalRepository.save(entry.build());
    }

    @Override
    public List<JournalEntry> getAll(Concern concern) {
        return journalRepository.findByConcern(concern, sortDesc);
    }

    @Override
    public List<JournalEntryDto> getLast(Concern concern, int count) {
        List<JournalEntry> entries = journalRepository.findByConcern(concern, PageRequest.of(0, count, sortDesc));
        return journalMapper.journalEntriesToDto(entries);
    }

    @Override
    public List<JournalEntryDto> getByIncident(Incident incident, Integer limit) {
        List<JournalEntry> entries = limit == null
                ? journalRepository.findByIncident(incident, sortDesc)
                : journalRepository.findByIncident(incident, PageRequest.of(0, limit, sortDesc));
        return journalMapper.journalEntriesToDto(entries);
    }

    @Override
    public List<JournalEntry> getByIncidentAsc(Incident incident) {
        return journalRepository.findByIncident(incident, sortAsc);
    }

    @Override
    public List<JournalEntryDto> getByUnit(Unit unit, Integer limit) {
        List<JournalEntry> entries = limit == null
                ? journalRepository.findByUnit(unit, sortDesc)
                : journalRepository.findByUnit(unit, PageRequest.of(0, limit, sortDesc));
        return journalMapper.journalEntriesToDto(entries);
    }

    @Override
    public List<JournalEntry> getByUnitAsc(Unit unit) {
        return journalRepository.findByUnit(unit, sortAsc);
    }

    @Override
    public List<JournalEntry> getByPatient(Patient patient) {
        return journalRepository.findByPatient(patient, patient.getIncidents(), sortDesc);
    }

    @Override
    public List<JournalEntry> getStatesByPatient(Patient patient) {
        return journalRepository.findStatesByPatient(patient, patient.getIncidents(), sortDesc);
    }

    @Override
    public List<JournalEntry> getByIncidentAndUnit(Incident incident, Unit unit) {
        return journalRepository.findByIncidentAndUnit(incident, unit, sortDesc);
    }

    @Override
    public Instant getLastTaskStateUpdate(Incident incident, Unit unit) {
        List<JournalEntry> last = journalRepository.findLast(PageRequest.of(0, 1), incident, unit,
                JournalEntryType.TASKSTATE_CHANGED, JournalEntryType.UNIT_ASSIGN, JournalEntryType.UNIT_DETACH,
                JournalEntryType.UNIT_AUTO_DETACH);
        return last.isEmpty() ? null : last.get(0).getTimestamp();
    }

    @Override
    public List<JournalEntryDto> getCustom(Concern concern) {
        List<JournalEntry> entries = journalRepository.findByConcernAndType(concern, JournalEntryType.CUSTOM, sortDesc);
        return journalMapper.journalEntriesToDto(entries);
    }

    @Override
    public List<JournalEntry> getCustomAsc(Concern concern) {
        return journalRepository.findByConcernAndType(concern, JournalEntryType.CUSTOM, sortAsc);
    }

    @Override
    public void updateForRemoval(Unit unit) {
        journalRepository.updateForRemoval(unit);
    }

    private JournalEntry.JournalEntryBuilder entry(JournalEntryType type, Concern concern, ChangesCollector changes) {
        return JournalEntry.builder()
                .username(getUser())
                .type(type)
                .concern(concern)
                .changes(changes != null ? changes.getData() : null);
    }

    private String getUser() {
        String username = AuthenticatedUser.getName();
        if (username == null) {
            log.info("Writing journal entry to database without authenticated user!");
        }
        return username;
    }
}
