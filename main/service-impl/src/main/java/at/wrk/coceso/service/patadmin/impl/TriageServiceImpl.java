package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Patient_;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.form.TriageForm;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import at.wrk.coceso.service.internal.PatientServiceInternal;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.internal.TriageServiceInternal;
import at.wrk.coceso.specification.PatientSearchSpecification;
import at.wrk.coceso.utils.DataAccessLogger;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class TriageServiceImpl implements TriageServiceInternal {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private IncidentRepository incidentRepository;

  @Autowired
  private PatientServiceInternal patientService;

  @Autowired
  private IncidentServiceInternal incidentService;

  @Autowired
  private PatadminService patadminService;

  @Override
  public List<Incident> getIncoming(Concern concern) {
    return incidentRepository.findIncoming(concern);
  }

  @Override
  public List<Incident> getIncoming(Unit unit) {
    return incidentRepository.findIncoming(unit.getConcern(), unit.getCall());
  }

  @Override
  public Patient getActivePatient(int patientId, User user) {
    Patient patient = patientService.getById(patientId, user);
    if (patient.isDone()) {
      throw new ErrorsException(Errors.PatientDone);
    }
    return patient;
  }

  @Override
  public List<Patient> getForAutocomplete(Concern concern, String query, String field, User user) {
    List<Patient> patients;

    switch (field) {
      case "externalId":
        patients = patientRepository.findAll(new PatientSearchSpecification(query, concern, false) {
          @Override
          protected Predicate buildKeywordPredicate(String keyword, Root<Patient> root, CriteriaBuilder builder) {
            return builder.or(
                builder.like(builder.lower(root.get(Patient_.externalId)), keyword + "%"),
                builder.like(builder.lower(root.get(Patient_.externalId)), "_-" + keyword + "%")
            );
          }
        });
        break;
      case "lastname":
        patients = patientRepository.findAll(new PatientSearchSpecification(query, concern, false) {
          @Override
          protected Predicate buildKeywordPredicate(String keyword, Root<Patient> root, CriteriaBuilder builder) {
            return builder.like(builder.lower(root.get(Patient_.lastname)), keyword + "%");
          }
        });
        break;
      case "firstname":
        patients = patientRepository.findAll(new PatientSearchSpecification(query, concern, false) {
          @Override
          protected Predicate buildKeywordPredicate(String keyword, Root<Patient> root, CriteriaBuilder builder) {
            return builder.like(builder.lower(root.get(Patient_.firstname)), keyword + "%");
          }
        });
        break;
      default:
        return null;
    }

    DataAccessLogger.logPatientAccess(patients, concern, query, user);
    return patients;
  }

  @Override
  public Patient takeover(int incidentId, User user, NotifyList notify) {
    Incident incident = incidentService.getById(incidentId);
    if (incident == null) {
      throw new ErrorsException(Errors.IncidentMissing);
    }
    if (incident.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    final Incident inc = incident;
    if ((incident.getType() != IncidentType.Task && incident.getType() != IncidentType.Transport)
        || !incident.getState().isDone() || incident.getAo() == null
        || !patadminService.getGroups(inc.getConcern()).stream().anyMatch(u -> u.getCall().equals(inc.getAo().getInfo()))) {
      throw new ErrorsException(Errors.IncidentNotAllowed);
    }

    if (incident.getPatient() != null) {
      if (incident.getPatient().isDone()) {
        throw new ErrorsException(Errors.PatientDone);
      }
      return incident.getPatient();
    }

    Patient patient = patientService.update(new Patient(), incident.getConcern(), user, notify);

    incident.setPatient(patient);
    incidentService.assignPatient(incident, patient, user, notify);

    return patient;
  }

  @Override
  public Patient update(TriageForm form, Concern concern, User user, NotifyList notify) {
    Patient old = form.getPatient() == null ? null : getActivePatient(form.getPatient(), user);

    Patient patient = prepare(form, old);
    patient = patientService.update(patient, concern, user, notify);

    if (patient.getIncidents() != null) {
      patient.getIncidents().size();
    }
    if (form.getGroup() != null && (patient.getGroup() == null
        || !patient.getGroup().stream().anyMatch(g -> g.getId().equals(form.getGroup())))) {
      incidentService.endTreatments(patient, user, notify);
      incidentService.createTreatment(patient, patadminService.getGroup(form.getGroup()), user, notify);
    }

    return patient;
  }

  private Patient prepare(TriageForm form, Patient old) {
    Patient patient = new Patient();

    if (old != null) {
      patient.setSex(old.getSex());
      patient.setInsurance(old.getInsurance());
      patient.setErtype(old.getErtype());
    }

    patient.setId(form.getPatient());
    patient.setLastname(form.getLastname());
    patient.setFirstname(form.getFirstname());
    patient.setExternalId(form.getExternalId());
    patient.setBirthday(form.getBirthday());
    patient.setNaca(form.getNaca());
    patient.setDiagnosis(form.getDiagnosis());
    patient.setInfo(form.getInfo());

    if (form.getMedinfo() != null) {
      patient.setMedinfo(new Medinfo(form.getMedinfo()));
    }
    return patient;
  }

}
