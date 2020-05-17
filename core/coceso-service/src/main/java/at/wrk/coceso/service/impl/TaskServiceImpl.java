package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.task.TaskStateDto;
import at.wrk.coceso.dto.task.TaskUpdateDto;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentClosedReason;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.journal.ChangesCollector;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.event.events.TaskEvent;
import at.wrk.coceso.exceptions.ImpossibleIncidentException;
import at.wrk.coceso.exceptions.ImpossibleTaskStateException;
import at.wrk.coceso.exceptions.SingleUnitTaskException;
import at.wrk.coceso.mapper.IncidentMapper;
import at.wrk.coceso.mapper.TaskMapper;
import at.wrk.coceso.mapper.UnitMapper;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.repository.TaskRepository;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.service.TaskService;
import at.wrk.fmd.mls.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final IncidentRepository incidentRepository;

    private final TaskMapper taskMapper;
    private final IncidentMapper incidentMapper;
    private final UnitMapper unitMapper;

    private final JournalService journalService;
    private final EventBus eventBus;

    @Autowired
    public TaskServiceImpl(final TaskRepository taskRepository, final IncidentRepository incidentRepository,
            final TaskMapper taskMapper, final IncidentMapper incidentMapper, final UnitMapper unitMapper,
            final JournalService journalService, final EventBus eventBus) {
        this.taskRepository = taskRepository;
        this.incidentRepository = incidentRepository;
        this.taskMapper = taskMapper;
        this.incidentMapper = incidentMapper;
        this.unitMapper = unitMapper;
        this.journalService = journalService;
        this.eventBus = eventBus;
    }

    @Override
    public void assignUnit(Incident incident, Unit unit) {
        if (unit.getTask(incident).isPresent()) {
            log.info("Unit {} is already assigned to incident {}. Assigning the unit again is skipped.", unit, incident);
            return;
        }

        assign(incident, unit, TaskState.Assigned, false);
    }

    @Override
    public void changeState(final Incident incident, final Unit unit, final TaskUpdateDto data) {
        log.debug("Trying to update unit {} and incident {} to '{}'", unit, incident, data);

        Optional<Task> task = unit.getTask(incident);
        if (data.getState() != TaskStateDto.Detached) {
            TaskState state = taskMapper.dtoToTaskState(data.getState());
            if (!incident.getType().isPossibleState(state)) {
                log.warn("New state {} not possible for unit {} and incident {}", state, unit, incident);
                throw new ImpossibleTaskStateException();
            }

            task.ifPresentOrElse(t -> updateState(t, state), () -> assign(incident, unit, state, false));
        } else if (task.isPresent()) {
            log.debug("Detaching unit {} from incident {}", unit, incident);
            detach(task.get());
        }
    }

    private void assign(final Incident incident, final Unit unit, TaskState state, boolean allowSingleUnit) {
        if (!allowSingleUnit && isSingleUnit(incident)) {
            // Assigning to single unit incidents is only possible on incident creation
            log.debug("Tried to assign unit {} to single unit incident {}", unit, incident);
            throw new SingleUnitTaskException();
        }

        log.debug("Unit {} was not assigned to incident {}, assigning with state {}.", unit, incident, state);

        // Detach incidents
        final Collection<Task> autoDetached = unit.getIncidents().stream()
                .filter(this::shouldAutoDetach)
                .peek(this::autoDetach)
                .collect(Collectors.toList());

        // Add a task
        Task task = taskRepository.save(new Task(incident, unit, state));
        unit.addTask(task);

        // Add journal entry
        journalService.logTask(JournalEntryType.UNIT_ASSIGN, task);

        // Notify listeners
        autoDetached.forEach(t -> notify(task, true));
        notify(task, false);
    }

    private void updateState(final Task task, final TaskState state) {
        if (state == task.getState()) {
            // State already set, do nothing
            log.debug("State for unit {} and incident {} was already set to {}", task.getUnit(), task.getIncident(), state);
            return;
        }

        log.debug("Unit {} was already assigned to incident {}, updating state to {}.", task.getUnit(), task.getIncident(), state);

        task.setState(state);
        journalService.logTask(JournalEntryType.TASKSTATE_CHANGED, task);
        notify(task, false);
    }

    private void detach(final Task task) {
        log.debug("Detaching unit {} from incident {}", task.getUnit(), task.getIncident());

        task.getUnit().removeTask(task);
        taskRepository.delete(task);

        autoCloseIncident(task.getIncident(), task.getState());

        journalService.logTaskDetach(JournalEntryType.UNIT_DETACH, task);
        notify(task, true);
    }

    private void notify(final Task task, boolean detached) {
        IncidentDto incident = incidentMapper.incidentToDto(task.getIncident());
        UnitDto unit = unitMapper.unitToDto(task.getUnit());
        TaskStateDto state = detached ? TaskStateDto.Detached : taskMapper.taskStateToDto(task.getState());

        eventBus.publish(new TaskEvent(incident, unit, state));
    }

    private boolean isSingleUnit(final Incident incident) {
        final IncidentType type = incident.getType();
        return type == IncidentType.Standby || type == IncidentType.ToHome;
    }

    private boolean shouldAutoDetach(final Task task) {
        final IncidentType type = task.getIncident().getType();
        return type == IncidentType.Standby || type == IncidentType.ToHome || type == IncidentType.Position;
    }

    private void autoDetach(final Task task) {
        final Unit unit = task.getUnit();
        final Incident incident = task.getIncident();

        log.debug("Auto-detach unit {} from incident {}", unit, incident);

        task.getUnit().removeTask(task);
        taskRepository.delete(task);
        journalService.logTask(JournalEntryType.UNIT_AUTO_DETACH, task);

        autoCloseIncident(incident, task.getState());
    }

    private void autoCloseIncident(final Incident incident, final TaskState previousState) {
        if (!incident.getUnits().isEmpty()) {
            // Other units assigned, do nothing
            return;
        }

        if (incident.getClosed() != null) {
            // Already marked as closed
            return;
        }

        if (previousState != TaskState.AAO && !isSingleUnit(incident)) {
            // Only close if single unit incident or last state was AAO
            return;
        }

        IncidentClosedReason reason = previousState == TaskState.AAO ? IncidentClosedReason.Closed : IncidentClosedReason.Cancelled;

        ChangesCollector changes = new ChangesCollector("incident");
        changes.put("closed", incident.getClosed(), reason);
        incident.setClosed(reason);

        journalService.logIncident(JournalEntryType.INCIDENT_AUTO_DONE, incident, changes);
    }

    @Override
    public void sendHome(Unit unit) {
        checkAssigned(unit, EnumSet.of(IncidentType.Standby, IncidentType.Position));
        // TODO This should only be possible if home is set
        createIncident(unit, IncidentType.ToHome, unit.getPosition(), unit.getHome());
    }

    @Override
    public void holdPosition(Unit unit) {
        checkAssigned(unit, Collections.emptySet());
        createIncident(unit, IncidentType.Position, null, unit.getPosition());
    }

    @Override
    public void standby(Unit unit) {
        checkAssigned(unit, EnumSet.of(IncidentType.ToHome, IncidentType.Position));
        createIncident(unit, IncidentType.Standby, null, unit.getPosition());
    }

    private void checkAssigned(Unit unit, Set<IncidentType> allowed) {
        if (unit.getIncidents().stream().anyMatch(t -> !allowed.contains(t.getIncident().getType()))) {
            throw new ImpossibleIncidentException();
        }
    }

    private void createIncident(Unit unit, IncidentType type, Point bo, Point ao) {
        Incident incident = new Incident();
        incident.setConcern(unit.getConcern());
        incident.setType(type);
        incident.setBo(bo);
        incident.setAo(ao);
        // TODO Set caller

        incident = incidentRepository.save(incident);
        assign(incident, unit, TaskState.Assigned, true);
    }
}
