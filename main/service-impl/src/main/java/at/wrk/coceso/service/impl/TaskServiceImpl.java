package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.service.hooks.HookService;
import at.wrk.coceso.service.internal.TaskServiceInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
class TaskServiceImpl implements TaskServiceInternal {

    private final static Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private HookService hookService;

    @Autowired
    private LogService logService;

    @Override
    public synchronized void changeState(int incident_id, int unit_id, TaskState state, User user, NotifyList notify) {
        Incident i = incidentService.getById(incident_id);
        Unit u = unitService.getById(unit_id);
        changeState(i, u, state, user, notify);
    }

    @Override
    public synchronized void changeState(final Incident incident, final Unit unit, final TaskState state, User user, NotifyList notify) {
        LOG.debug("{}: Trying to update unit {} and incident {} to '{}'", user, unit, incident, state);

        if (incident == null || unit == null) {
            LOG.info("{}: Unit/incident not found", user);
            throw new ErrorsException(Errors.EntityMissing);
        }

        if (!Objects.equals(incident.getConcern(), unit.getConcern())) {
            LOG.info("{}: Combination Unit {}/incident {} in different concerns", user, unit, incident);
            throw new ErrorsException(Errors.ConcernMismatch);
        }

        if (incident.getConcern().isClosed()) {
            LOG.info("{}: Tried to change TaskState in closed concern", user);
            throw new ErrorsException(Errors.ConcernClosed);
        }

        if (incident.getType() == IncidentType.Treatment) {
            LOG.info("{}: Tried to change TaskState for treatment incident", user);
            throw new ErrorsException(Errors.IncidentNotAllowed);
        }

        if (!incident.getType().isPossibleState(state)) {
            LOG.warn("{}: TaskService.changeState(): New state not possible for Unit {}/incident {}", user, unit, incident);
            throw new ErrorsException(Errors.ImpossibleTaskState);
        }

        Unit updatedUnit;
        if (incident.getUnits() == null || incident.getUnits().get(unit) == null) {
            LOG.debug("{}: Unit {} was not assigned previously to incident {}. Assigning unit to incident with TaskState {}.", user, unit, incident, state);
            updatedUnit = assign(incident, unit, state, user, notify);
        } else {
            LOG.debug("{}: Unit {} was already assigned to incident {}. TaskState is updated to {}.", user, unit, incident, state);
            updatedUnit = setState(incident, unit, state, user, notify);
        }

        if (updatedUnit != null) {
            updatedUnit = unitRepository.saveAndFlush(updatedUnit);

            notify.add(incident);
            notify.add(updatedUnit);
        }
    }

    @Override
    public void uncheckedChangeState(final Incident incident, final Unit unit, final TaskState state, final User user, final NotifyList notify) {
        if (state == TaskState.Detached) {
            unit.removeIncident(incident);
        } else {
            unit.addIncident(incident, state);
        }

        Unit updatedUnit = unitRepository.saveAndFlush(unit);
        notify.add(incident);
        notify.add(updatedUnit);
    }

    private Unit assign(final Incident incident, final Unit unit, TaskState state, final User user, final NotifyList notify) {
        if (state == TaskState.Detached) {
            // We are detaching, but unit isn't assigned anyway: Nothing to do anymore
            return null;
        }

        // HoldPosition and Standby can't be assigned to multiple Units
        if (incident.getType().isSingleUnit() && incident.getUnits() != null && !incident.getUnits().isEmpty()) {
            LOG.debug("{}: Tried to assign multiple units ({}) to single unit incident {}", user, unit, incident);
            throw new ErrorsException(Errors.MultipleUnits);
        }

        // Auto-Detach unit from all SingleUnit Incidents and Relocation
        unit.getIncidents()
                .keySet()
                .stream()
                .filter(this::isAutoDetachApplicable)
                .forEach(incidentOfUnit -> autoDetachUnitFromIncident(unit, user, notify, incidentOfUnit));

        TaskState effectiveTaskState = hookService.callTaskStateChanged(incident, unit, state, user, notify);

        // Add incident to unit's task list
        unit.addIncident(incident, effectiveTaskState);

        logService.logAuto(user, LogEntryType.UNIT_ASSIGN, unit.getConcern(), unit, incident, effectiveTaskState);

        return unit;
    }

    private void autoDetachUnitFromIncident(final Unit unit, final User user, final NotifyList notify, final Incident incident) {
        LOG.debug("{}: Auto-detach unit #{}, incident #{}", user, unit.getId(), incident.getId());
        logService.logAuto(user, LogEntryType.UNIT_AUTO_DETACH, unit.getConcern(), unit, incident, TaskState.Detached);

        hookService.callTaskStateChanged(incident, unit, TaskState.Detached, user, notify);
        unit.removeIncident(incident);

        notify.add(incident);
    }

    private boolean isAutoDetachApplicable(final Incident inc) {
        return inc.getType().isSingleUnit() || inc.getType() == IncidentType.Relocation;
    }

    private Unit setState(Incident i, Unit u, TaskState state, User user, NotifyList notify) {
        logService.logAuto(user, state == TaskState.Detached ? LogEntryType.UNIT_DETACH : LogEntryType.TASKSTATE_CHANGED, i.getConcern(), u, i, state);

        // Call additional hooks
        state = hookService.callTaskStateChanged(i, u, state, user, notify);

        if (state == TaskState.Detached) {
            LOG.debug("{}: Detaching unit {} from incident {}", user, u, i);
            u.removeIncident(i);
        } else {
            // Only save if not already done by assignUnit
            LOG.debug("{}: Updating TaskState for unit {}/incident {} to {}", user, u, i, state);
            u.addIncident(i, state);
        }

        return u;
    }

}
