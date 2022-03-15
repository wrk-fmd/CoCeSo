package at.wrk.coceso.auth;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.AccessLevel;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Component("auth")
@Transactional
public class AuthorizationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationProvider.class);

    @Autowired
    private UnitService unitService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private ConcernService concernService;

    @Autowired
    private PatientService patientService;

    public boolean hasAccessLevel(final AccessLevel level) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities() != null && level.isGrantedFor(auth.getAuthorities());
    }

    public boolean hasAccessLevel(final String level) {
        try {
            return level != null && hasAccessLevel(AccessLevel.valueOf(level));
        } catch (IllegalArgumentException e) {
            LOG.warn("Tried to check invalid AccessLevel '{}'", level);
            return false;
        }
    }

    public boolean hasPermission(final Object target, final Object perm) {
        try {
            AccessLevel level = AccessLevel.valueOf(perm);
            if (target instanceof Unit) {
                return hasPermission((Unit) target, level);
            }
            if (target instanceof Incident) {
                return hasPermission((Incident) target, level);
            }
            if (target instanceof Concern) {
                return hasPermission((Concern) target, level);
            }
            if (target instanceof Patient) {
                return hasPermission((Patient) target, level);
            }

            LOG.warn("Tried to check permission on unknown class '{}'. Access is denied.", target.getClass().getName());
        } catch (IllegalArgumentException e) {
            LOG.warn("Exception on checking permission", e);
        }
        return false;
    }

    public boolean hasPermission(final Serializable id, final String type, final Object perm) {
        try {
            AccessLevel level = AccessLevel.valueOf(perm);
            int entityId = (int) id;

            if (type.equals(Unit.class.getName())) {
                return hasPermission(unitService.getById(entityId), level);
            }
            if (type.equals(Incident.class.getName())) {
                return hasPermission(incidentService.getById(entityId), level);
            }
            if (type.equals(Concern.class.getName())) {
                return hasPermission(concernService.getById(entityId), level);
            }
            if (type.equals(Patient.class.getName())) {
                return hasPermission(patientService.getByIdNoLog(entityId), level);
            }

            LOG.warn("Tried to check permission on unknown class '{}'. Access is denied.", type);
        } catch (IllegalArgumentException | ErrorsException e) {
            LOG.warn("Exception on checking permission", e);
        }

        return false;
    }

    public boolean hasPermission(final Unit unit, final AccessLevel level) {
        if (level == null) {
            return false;
        }
        if (hasAccessLevel(level)) {
            return true;
        }

        User user = getUser();
        if (user == null || !user.isAllowLogin() || unit == null || unit.getId() == null || unit.getConcern().isClosed()) {
            return false;
        }

        return checkLocalAccess(user, unit, level) || checkConcernAccess(user, unit.getConcern(), level);
    }

    public boolean hasPermission(final Incident incident, final AccessLevel level) {
        if (level == null) {
            return false;
        }
        if (hasAccessLevel(level)) {
            return true;
        }

        User user = getUser();
        if (user == null || !user.isAllowLogin() || incident == null || incident.getId() == null || incident.getConcern().isClosed()) {
            return false;
        }

        return incident.getUnits().keySet().stream().anyMatch(u -> checkLocalAccess(user, u, level))
                || checkConcernAccess(user, incident.getConcern(), level);
    }

    public boolean hasPermission(final Concern concern, final AccessLevel level) {
        if (level == null) {
            return false;
        }
        if (hasAccessLevel(level)) {
            return true;
        }

        User user = getUser();
        LOG.trace("Checking access level '{}' for user '{}' in the context of concern '{}'.", level, user, concern);
        if (user == null || !user.isAllowLogin() || Concern.isClosedOrNull(concern)) {
            LOG.trace("  Access is rejected because user is disabled or concern is already closed.");
            return false;
        }

        return checkConcernAccess(user, concern, level);
    }

    public boolean hasPermission(final Patient patient, final AccessLevel level) {
        if (level == null) {
            return false;
        }
        if (hasAccessLevel(level)) {
            return true;
        }

        User user = getUser();
        if (user == null || !user.isAllowLogin() || patient == null || patient.getId() == null || patient.getConcern().isClosed()) {
            return false;
        }

        return checkConcernAccess(user, patient.getConcern(), level);
    }

    private boolean checkConcernAccess(final User user, final Concern concern, final AccessLevel level) {
        return level.allowConcernWide() &&
                unitService.getByConcernUser(concern, user)
                        .stream()
                        .anyMatch(u -> level.isGrantedFor(u.getType(), false));
    }

    private boolean checkLocalAccess(final User user, final Unit unit, final AccessLevel level) {
        return (unit.getCrew().contains(user) && level.isGrantedFor(unit.getType(), true));
    }

    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getPrincipal() instanceof User) ? (User) auth.getPrincipal() : null;
    }

}
