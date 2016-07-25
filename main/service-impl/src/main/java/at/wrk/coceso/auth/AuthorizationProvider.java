package at.wrk.coceso.auth;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.AccessLevel;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.MedinfoRepository;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.UnitService;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("auth")
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

  @Autowired
  private MedinfoRepository medinfoRepository;

  public boolean hasAccessLevel(AccessLevel level) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.getAuthorities() != null && level.isGrantedFor(auth.getAuthorities());
  }

  public boolean hasAccessLevel(String level) {
    try {
      return level != null ? hasAccessLevel(AccessLevel.valueOf(level)) : false;
    } catch (IllegalArgumentException e) {
      LOG.warn("Tried to check invalid AccessLevel '{}'", level);
      return false;
    }
  }

  public boolean hasPermission(Object target, Object perm) {
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
      if (target instanceof Medinfo) {
        return hasPermission((Medinfo) target, level);
      }
      LOG.warn("Tried to check permission on class {}", target.getClass());
    } catch (IllegalArgumentException e) {
      LOG.warn("Exception on checking permission", e);
    }
    return false;
  }

  public boolean hasPermission(Serializable id, String type, Object perm) {
    try {
      AccessLevel level = AccessLevel.valueOf(perm);
      int pk = (int) id;

      if (type.equals(Unit.class.getName())) {
        return hasPermission(unitService.getById(pk), level);
      }
      if (type.equals(Incident.class.getName())) {
        return hasPermission(incidentService.getById(pk), level);
      }
      if (type.equals(Concern.class.getName())) {
        return hasPermission(concernService.getById(pk), level);
      }
      if (type.equals(Patient.class.getName())) {
        return hasPermission(patientService.getByIdNoLog(pk), level);
      }
      if (type.equals(Medinfo.class.getName())) {
        return hasPermission(medinfoRepository.findOne(pk), level);
      }
      LOG.warn("Tried to check permission on class {}", type);
    } catch (IllegalArgumentException | ErrorsException e) {
      LOG.warn("Exception on checking permission", e);
    }
    return false;
  }

  public boolean hasPermission(Unit unit, AccessLevel level) {
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

  public boolean hasPermission(Incident incident, AccessLevel level) {
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

  public boolean hasPermission(Concern concern, AccessLevel level) {
    if (level == null) {
      return false;
    }
    if (hasAccessLevel(level)) {
      return true;
    }

    User user = getUser();
    if (user == null || !user.isAllowLogin() || Concern.isClosed(concern)) {
      return false;
    }

    return checkConcernAccess(user, concern, level);
  }

  public boolean hasPermission(Patient patient, AccessLevel level) {
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

  public boolean hasPermission(Medinfo medinfo, AccessLevel level) {
    if (level == null) {
      return false;
    }
    if (hasAccessLevel(level)) {
      return true;
    }

    User user = getUser();
    if (user == null || !user.isAllowLogin() || medinfo == null || medinfo.getId() == null || medinfo.getConcern().isClosed()) {
      return false;
    }

    return checkConcernAccess(user, medinfo.getConcern(), level);
  }

  private boolean checkConcernAccess(User user, Concern concern, AccessLevel level) {
    return level.allowConcernWide() && unitService.getByConcernUser(concern, user)
        .stream().anyMatch(u -> level.isGrantedFor(u.getType(), false));
  }

  private boolean checkLocalAccess(User user, Unit unit, AccessLevel level) {
    return (unit.getCrew().contains(user) && level.isGrantedFor(unit.getType(), true));
  }

  public User getUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (auth != null && auth.getPrincipal() instanceof User) ? (User) auth.getPrincipal() : null;
  }

}
