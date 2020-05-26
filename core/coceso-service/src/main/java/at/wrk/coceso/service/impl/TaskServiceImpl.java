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
import at.wrk.coceso.exceptions.TaskInvalidUnitException;
import at.wrk.coceso.mapper.IncidentMapper;
import at.wrk.coceso.mapper.TaskMapper;
import at.wrk.coceso.mapper.UnitMapper;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.repository.TaskRepository;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.utils.AuthenticatedUser;
import at.wrk.fmd.mls.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
            detach(task.get(), false);
        }
    }

    /**
     * Assign a new unit to an incident
     *
     * @param incident The incident
     * @param unit The unit, which must be portable
     * @param state The TaskState to set
     * @param allowSingleUnit Whether assigning to single-unit incidents is allowed
     */
    private void assign(final Incident incident, final Unit unit, TaskState state, boolean allowSingleUnit) {
        if (!unit.isPortable()) {
            // Only portable units can be assigned
            log.debug("Tried to assign non-portable unit {} to incident {}", unit, incident);
            throw new TaskInvalidUnitException();
        }

        if (!allowSingleUnit && isSingleUnit(incident)) {
            // Assigning to single unit incidents is only possible on incident creation
            log.debug("Tried to assign unit {} to single unit incident {}", unit, incident);
            throw new SingleUnitTaskException();
        }

        log.debug("Unit {} was not assigned to incident {}, assigning with state {}.", unit, incident, state);

        // Detach other incidents assigned to the unit
        final Collection<Task> autoDetached = unit.getIncidents().stream()
                .filter(this::shouldAutoDetach)
                .peek(t -> detach(t, true))
                .collect(Collectors.toList());

        // Execute callbacks and check if we should proceed
        if (executeCallbacks(incident, unit, state)) {
            // Notify about the auto detached incidents, even if we don't proceed
            autoDetached.forEach(t -> notify(t, true));
            return;
        }

        // Set the incident to non-closed
        incident.setClosed(null);
        incident.setEnded(null);

        // Add a task
        Task task = taskRepository.save(new Task(incident, unit, state));
        unit.addTask(task);

        // Add journal entry
        journalService.logTask(JournalEntryType.UNIT_ASSIGN, task);

        // Notify listeners
        autoDetached.forEach(t -> notify(t, true));
        notify(task, false);
    }

    /**
     * Update the state of an already assigned task
     *
     * @param task The task (containing unit and incident information)
     * @param state The new state
     */
    private void updateState(final Task task, final TaskState state) {
        if (state == task.getState()) {
            // State already set, do nothing
            log.debug("State for unit {} and incident {} was already set to {}", task.getUnit(), task.getIncident(), state);
            return;
        }

        // Execute callbacks and check if we should proceed
        if (executeCallbacks(task.getIncident(), task.getUnit(), state)) {
            return;
        }

        // Update the state
        log.debug("Unit {} was already assigned to incident {}, updating state to {}.", task.getUnit(), task.getIncident(), state);
        task.setState(state);
        task.setUpdated(Instant.now());

        // Add journal entry
        journalService.logTask(JournalEntryType.TASKSTATE_CHANGED, task);

        // Notify listeners
        notify(task, false);
    }

    @Override
    public void detachAll(Incident incident, boolean auto) {
        log.debug("Detaching all units from incident {}", incident);

        if (incident.getUnits() == null) {
            return;
        }

        final Collection<Task> tasks = Set.copyOf(incident.getUnits());
        taskRepository.deleteAll(tasks);

        incident.setStateChange(Instant.now());

        tasks.forEach(task -> {
            task.getUnit().removeTask(task);
            journalService.logTaskDetach(auto ? JournalEntryType.UNIT_AUTO_DETACH : JournalEntryType.UNIT_DETACH, task);
            notify(task, true);
        });
    }

    /**
     * Detach the task
     *
     * @param task The task (containing unit and incident information)
     * @param auto true iff the system initiated the detaching
     */
    private void detach(final Task task, final boolean auto) {
        log.debug("Detaching unit {} from incident {}", task.getUnit(), task.getIncident());

        task.getUnit().removeTask(task);
        taskRepository.delete(task);

        task.getIncident().setStateChange(Instant.now());
        autoCloseIncident(task.getIncident(), task.getState());

        journalService.logTaskDetach(auto ? JournalEntryType.UNIT_AUTO_DETACH : JournalEntryType.UNIT_DETACH, task);
        notify(task, true);
    }

    /**
     * Notify subscribers that a task has changed
     *
     * @param task The updated task
     * @param detached True iff the task has been detached
     */
    private void notify(final Task task, boolean detached) {
        IncidentDto incident = incidentMapper.incidentToDto(task.getIncident());
        UnitDto unit = unitMapper.unitToDto(task.getUnit());
        TaskStateDto state = detached ? TaskStateDto.Detached : taskMapper.taskStateToDto(task.getState());

        eventBus.publish(new TaskEvent(incident, unit, state));
    }

    /**
     * Check whether only a single unit can be attached to an incident
     */
    private boolean isSingleUnit(final Incident incident) {
        final IncidentType type = incident.getType();
        return type == IncidentType.Standby || type == IncidentType.ToHome;
    }

    /**
     * Check whether an task should be detached if the unit is assigned to another incident
     */
    private boolean shouldAutoDetach(final Task task) {
        final IncidentType type = task.getIncident().getType();
        return type == IncidentType.Standby || type == IncidentType.ToHome || type == IncidentType.Position;
    }

    /**
     * Execute callbacks before creating/updating a task
     *
     * @param incident The incident of the task
     * @param unit The unit of the task
     * @param state The new state of the task
     * @return True iff the update should be aborted
     */
    private boolean executeCallbacks(final Incident incident, final Unit unit, final TaskState state) {
        setUnitPosition(incident, unit, state);
        updateIncidentTimestamps(incident, state);
        return detachToHome(incident, state);
    }

    /**
     * Set the unit's position if ABO/AAO
     *
     * @param incident The incident of the task
     * @param unit The unit of the task
     * @param state The new state of the task
     */
    private void setUnitPosition(final Incident incident, final Unit unit, final TaskState state) {
        // Set the unit's position if ABO/AAO
        Point position = null;
        if (state == TaskState.ABO) {
            position = incident.getBo();
        } else if (state == TaskState.AAO) {
            position = incident.getAo();
        }

        if (position == null || Point.infoEquals(position, unit.getPosition())) {
            return;
        }

        log.debug("Position auto-set for unit {}", unit);

        ChangesCollector changes = new ChangesCollector("unit");
        changes.put("position", Point.toStringOrNull(unit.getPosition()), Point.toStringOrNull(position));
        unit.setPosition(position);

        journalService.logUnit(JournalEntryType.UNIT_AUTOSET_POSITION, unit, changes);
    }

    /**
     * Update the timestamps of the incident
     *
     * @param incident The incident of the task
     * @param state The new state of the task
     */
    private void updateIncidentTimestamps(final Incident incident, final TaskState state) {
        incident.setStateChange(Instant.now());
        if (incident.getArrival() == null && state != TaskState.Assigned && state != TaskState.ZBO) {
            incident.setArrival(Instant.now());
        }
    }

    /**
     * Detach ToHome incidents when state is set to ABO
     *
     * @param incident The incident of the task
     * @param state The new state of the task
     * @return True iff the incident was detached
     */
    private boolean detachToHome(final Incident incident, final TaskState state) {
        if (incident.getType() != IncidentType.ToHome || state != TaskState.ABO) {
            return false;
        }

        detachAll(incident, false);
        autoCloseIncident(incident, state);
        return true;
    }

    /**
     * Auto-close the incident after detaching the last unit
     *
     * @param incident The incident of the task
     * @param previousState The previous task state
     */
    private void autoCloseIncident(final Incident incident, final TaskState previousState) {
        if (!incident.getUnits().isEmpty()) {
            // Other units assigned, do nothing
            return;
        }

        if (incident.getClosed() != null) {
            // Already marked as closed
            return;
        }

        // TODO This should also consider "required units" as soon as the feature is implemented
        IncidentClosedReason reason;
        switch (incident.getType()) {
            case Task:
            case Transport:
                // Only auto-close if at least at ZAO, earlier has to be done manually
                if (previousState != TaskState.ZAO && previousState != TaskState.AAO) {
                    return;
                }
                reason = IncidentClosedReason.Closed;
                break;
            default:
                reason = previousState == TaskState.ABO ? IncidentClosedReason.Closed : IncidentClosedReason.Cancelled;
                break;
        }

        ChangesCollector changes = new ChangesCollector("incident");
        changes.put("closed", incident.getClosed(), reason);
        incident.setClosed(reason);
        incident.setEnded(Instant.now());

        journalService.logIncident(JournalEntryType.INCIDENT_AUTO_DONE, incident, changes);
    }

    @Override
    public void sendHome(Unit unit) {
        checkAssigned(unit, EnumSet.of(IncidentType.Standby, IncidentType.Position));
        // TODO This should only be possible if home is set
        createIncident(unit, IncidentType.ToHome, unit.getHome());
    }

    @Override
    public void holdPosition(Unit unit) {
        checkAssigned(unit, Collections.emptySet());
        createIncident(unit, IncidentType.Position, unit.getPosition());
    }

    @Override
    public void standby(Unit unit) {
        checkAssigned(unit, EnumSet.of(IncidentType.ToHome, IncidentType.Position));
        createIncident(unit, IncidentType.Standby, unit.getPosition());
    }

    private void checkAssigned(Unit unit, Set<IncidentType> allowed) {
        if (unit.getIncidents().stream().anyMatch(t -> !allowed.contains(t.getIncident().getType()))) {
            throw new ImpossibleIncidentException();
        }
    }

    private void createIncident(Unit unit, IncidentType type, Point bo) {
        Incident incident = new Incident();
        incident.setConcern(unit.getConcern());
        incident.setType(type);
        incident.setBo(bo);
        incident.setCaller(AuthenticatedUser.getName());

        incident = incidentRepository.save(incident);
        assign(incident, unit, TaskState.Assigned, true);
    }
}
