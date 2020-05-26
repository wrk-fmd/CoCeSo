package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.incident.IncidentBriefDto;
import at.wrk.coceso.dto.incident.IncidentCreateDto;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.incident.IncidentUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentClosedReason;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.journal.ChangesCollector;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.event.events.IncidentEvent;
import at.wrk.coceso.event.events.PatientAssignedEvent;
import at.wrk.coceso.mapper.IncidentMapper;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.utils.AuthenticatedUser;
import at.wrk.fmd.mls.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
class IncidentServiceImpl implements IncidentService {

    private final static Sort SORT = Sort.by(Sort.Direction.ASC, "id");

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;
    private final TaskService taskService;
    private final PointService pointService;
    private final JournalService journalService;
    private final EventBus eventBus;

    @Autowired
    public IncidentServiceImpl(final IncidentRepository incidentRepository, final IncidentMapper incidentMapper,
            final TaskService taskService, final PointService pointService, final JournalService journalService, final EventBus eventBus) {
        this.incidentRepository = incidentRepository;
        this.incidentMapper = incidentMapper;
        this.taskService = taskService;
        this.pointService = pointService;
        this.journalService = journalService;
        this.eventBus = eventBus;
    }

    @Override
    public List<Incident> getAll(Concern concern) {
        return incidentRepository.findByConcern(concern);
    }

    @Override
    public List<Incident> getAllSorted(Concern concern) {
        return incidentRepository.findByConcern(concern, SORT);
    }

    @Override
    public List<IncidentDto> getAllRelevant(Concern concern) {
        return incidentRepository.findRelevant(concern).stream()
                .map(incidentMapper::incidentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> getAllForReport(Concern concern) {
        return incidentRepository.findNonSingleUnit(concern, SORT);
    }

    @Override
    public List<Incident> getAllForDump(Concern concern) {
        return incidentRepository.findActiveNonSingleUnit(concern, SORT);
    }

    @Override
    public List<Incident> getAllTransports(Concern concern) {
        return incidentRepository.findTransports(concern, SORT);
    }

    @Override
    public List<Incident> getAllActive(Concern concern) {
        return incidentRepository.findActive(concern, SORT);
    }

    @Override
    public Map<Incident, TaskState> getRelated(Unit unit) {
        return incidentRepository.findByIdIn(incidentRepository.findRelated(unit)).stream()
                .collect(Collectors.toMap(Function.identity(), i -> unit.getTask(i).map(Task::getState).orElse(TaskState.Detached)));
    }

    @Override
    public IncidentBriefDto create(Concern concern, IncidentCreateDto data) {
        log.debug("{}: Creating incident with data: {}", AuthenticatedUser.getName(), data);

        ChangesCollector changes = new ChangesCollector("incident");
        Incident incident = new Incident();

        // Set properties
        incident.setConcern(concern);

        IncidentType type = incidentMapper.typeDtoToType(data.getType());
        changes.put("type", type);
        incident.setType(type);

        IncidentClosedReason closed = incidentMapper.closedReasonDtoClosedReason(data.getClosed());
        if (closed != null) {
            changes.put("closed", closed);
            incident.setClosed(closed);
            incident.setEnded(Instant.now());
        }

        if (data.isPriority()) {
            changes.put("priority", true);
            incident.setPriority(true);
        }

        if (data.isBlue()) {
            changes.put("blue", true);
            incident.setBlue(true);
        }

        Point bo = pointService.getPoint(concern, data.getBo());
        if (!Point.isEmpty(bo)) {
            changes.put("bo", bo.toString());
            incident.setBo(bo);
        }

        Point ao = pointService.getPoint(concern, data.getAo());
        if (!Point.isEmpty(ao)) {
            changes.put("ao", ao.toString());
            incident.setAo(ao);
        }

        if (data.getCasusNr() != null) {
            changes.put("casusNr", data.getCasusNr());
            incident.setCasusNr(data.getCasusNr());
        }

        if (data.getInfo() != null) {
            changes.put("info", data.getInfo());
            incident.setInfo(data.getInfo());
        }

        if (data.getCaller() != null) {
            changes.put("caller", data.getCaller());
            incident.setCaller(data.getCaller());
        }

        if (data.getSection() != null && !data.getSection().isEmpty()) {
            if (concern.containsSection(data.getSection())) {
                changes.put("section", data.getSection());
                incident.setSection(data.getSection());
            } else {
                log.info("Tried to create incident with unknown section: '{}'", data.getSection());
            }
        }

        incident = incidentRepository.save(incident);
        journalService.logIncident(JournalEntryType.INCIDENT_CREATE, incident, changes);
        eventBus.publish(new IncidentEvent(incidentMapper.incidentToDto(incident)));

        return incidentMapper.incidentToBriefDto(incident);
    }

    @Override
    public void update(Incident incident, IncidentUpdateDto data) {
        log.info("{}: Updating incident {} with data {}", AuthenticatedUser.getName(), incident, data);

        ChangesCollector changes = new ChangesCollector("incident");
        boolean removeUnits = false;

        // Set updateable properties

        // TODO Check whether type change is allowed
        IncidentType type = incidentMapper.typeDtoToType(data.getType());
        if (type != null && type != incident.getType()) {
            changes.put("type", incident.getType(), type);
            incident.setType(type);
        }

        if (data.getClosed() != null) {
            IncidentClosedReason closed = incidentMapper.closedReasonDtoClosedReason(data.getClosed());
            if (closed != incident.getClosed()) {
                changes.put("closed", incident.getClosed(), closed);
                incident.setClosed(closed);
                if (closed == null) {
                    incident.setEnded(null);
                } else if (incident.getEnded() == null) {
                    incident.setEnded(Instant.now());
                }

                removeUnits = closed != null;
            }
        }

        if (data.getPriority() != null && !data.getPriority().equals(incident.isPriority())) {
            changes.put("priority", incident.isPriority(), data.getPriority());
            incident.setPriority(data.getPriority());
        }

        if (data.getBlue() != null && !data.getBlue().equals(incident.isBlue())) {
            changes.put("blue", incident.isBlue(), data.getBlue());
            incident.setBlue(data.getBlue());
        }

        Point bo = pointService.getPoint(incident.getConcern(), data.getBo());
        if (data.getBo() != null && !Point.infoEquals(bo, incident.getBo())) {
            changes.put("bo", Point.toStringOrNull(incident.getBo()), Point.toStringOrNull(bo));
            incident.setBo(bo);
        }

        Point ao = pointService.getPoint(incident.getConcern(), data.getAo());
        if (data.getAo() != null && !Point.infoEquals(ao, incident.getAo())) {
            changes.put("ao", Point.toStringOrNull(incident.getAo()), Point.toStringOrNull(ao));
            incident.setAo(ao);
        }

        if (data.getCasusNr() != null && !data.getCasusNr().equals(incident.getCasusNr())) {
            changes.put("casusNr", incident.getCasusNr(), data.getCasusNr());
            incident.setCasusNr(data.getCasusNr());
        }

        if (data.getInfo() != null && !data.getInfo().equals(incident.getInfo())) {
            changes.put("info", incident.getInfo(), data.getInfo());
            incident.setInfo(data.getInfo());
        }

        if (data.getCaller() != null && !data.getCaller().equals(incident.getCaller())) {
            changes.put("caller", incident.getCaller(), data.getCaller());
            incident.setCaller(data.getCaller());
        }

        if (data.getSection() != null) {
            String section = data.getSection().isEmpty() ? null : data.getSection();
            if (!Objects.equals(section, incident.getSection())) {
                if (section == null || incident.getConcern().containsSection(data.getSection())) {
                    changes.put("section", incident.getSection(), section);
                    incident.setSection(section);
                } else {
                    log.info("Tried to set unknown section for incident: '{}'", section);
                }
            }
        }

        if (!changes.isEmpty()) {
            journalService.logIncident(JournalEntryType.INCIDENT_UPDATE, incident, changes);
            eventBus.publish(new IncidentEvent(incidentMapper.incidentToDto(incident)));
        }

        if (removeUnits) {
            this.taskService.detachAll(incident, true);
        }
    }

    @Override
    public void assignPatient(final Incident incident, final Patient patient) {
        incident.setPatient(patient);
        journalService.logPatientAssign(JournalEntryType.PATIENT_ASSIGN, incident);
        eventBus.publish(new PatientAssignedEvent(incidentMapper.incidentToDto(incident)));
    }
}
