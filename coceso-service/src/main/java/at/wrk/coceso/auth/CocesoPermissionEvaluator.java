package at.wrk.coceso.auth;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class CocesoPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        try {
            AccessLevel level = getAccessLevel(permission);

            if (hasGlobalPermission(authentication, level)) {
                // Fast-track global permission check without touching any entity
                return true;
            } else if (target == null) {
                // No global permission and no target given
                return false;
            }

            if (target instanceof Unit) {
                return hasUnitPermission(authentication, ((Unit) target).getId(), level);
            }
            if (target instanceof Incident) {
                return hasIncidentPermission(authentication, ((Incident) target).getId(), level);
            }
            if (target instanceof Patient) {
                return hasPatientPermission(authentication, ((Patient) target).getId(), level);
            }
            if (target instanceof Concern) {
                return hasConcernPermission(authentication, ((Concern) target).getId(), level);
            }

            log.warn("Tried to check permission on class {}", target.getClass());
        } catch (IllegalArgumentException e) {
            log.warn("Exception on checking permission", e);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        try {
            AccessLevel level = getAccessLevel(permission);

            if (hasGlobalPermission(authentication, level)) {
                // Fast-track global permission check without touching any entity
                return true;
            } else if (targetType == null || !(targetId instanceof Long)) {
                // No global permission and no target given
                return false;
            }

            if (targetType.equals(Unit.class.getSimpleName())) {
                return hasUnitPermission(authentication, (long) targetId, level);
            }
            if (targetType.equals(Incident.class.getName())) {
                return hasIncidentPermission(authentication, (long) targetId, level);
            }
            if (targetType.equals(Patient.class.getName())) {
                return hasPatientPermission(authentication, (long) targetId, level);
            }
            if (targetType.equals(Concern.class.getName())) {
                return hasConcernPermission(authentication, (long) targetId, level);
            }

            log.warn("Tried to check permission on class {}", targetType);
        } catch (IllegalArgumentException e) {
            log.warn("Exception on checking permission", e);
        }

        return false;
    }

    private boolean hasGlobalPermission(Authentication authentication, AccessLevel level) {
        return level.isGrantedForRoles(authentication.getAuthorities());
    }

    private boolean hasUnitPermission(Authentication authentication, long unitId, AccessLevel level) {
        // TODO
        return false;
    }

    private boolean hasIncidentPermission(Authentication authentication, long unitId, AccessLevel level) {
        // TODO
        return false;
    }

    private boolean hasPatientPermission(Authentication authentication, long unitId, AccessLevel level) {
        // TODO
        return false;
    }

    private boolean hasConcernPermission(Authentication authentication, long unitId, AccessLevel level) {
        // TODO
        return false;
    }

    private AccessLevel getAccessLevel(Object level) {
        if (level instanceof AccessLevel) {
            return (AccessLevel) level;
        }
        if (level instanceof String) {
            return AccessLevel.valueOf((String) level);
        }
        throw new IllegalArgumentException("AccessLevel can only be created from String");
    }
}
