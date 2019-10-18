package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Patient_;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.point.UnitPoint;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import at.wrk.coceso.service.internal.PatientServiceInternal;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.internal.RegistrationServiceInternal;
import at.wrk.coceso.specification.PatientSearchSpecification;
import at.wrk.coceso.utils.DataAccessLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Service
@Transactional
class RegistrationServiceImpl implements RegistrationServiceInternal {

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

    private final DataAccessLogger dataAccessLogger;

    @Autowired
    RegistrationServiceImpl(final DataAccessLogger dataAccessLogger) {
        this.dataAccessLogger = dataAccessLogger;
    }

    @Override
    public List<Incident> getIncoming(Concern concern) {
        return incidentRepository.findIncoming(concern);
    }

    @Override
    public List<Incident> getIncoming(Unit unit) {
        return incidentRepository.findIncoming(unit.getConcern(), unit.getId());
    }

    @Override
    public long getTreatmentCount(Concern concern) {
        return incidentRepository.countTreatments(concern);
    }

    @Override
    public long getTransportCount(Concern concern) {
        return incidentRepository.countTransports(concern);
    }

    @Override
    public Patient getActivePatient(int patientId) {
        Patient patient = patientService.getById(patientId);
        if (patient.isDone()) {
            throw new ErrorsException(Errors.PatientDone);
        }
        return patient;
    }

    @Override
    public List<Patient> getForAutocomplete(final Concern concern, final String query, final String field) {
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
                patients = patientRepository.findAll(createSpecification(concern, query, Patient_.lastname));
                break;
            case "firstname":
                patients = patientRepository.findAll(createSpecification(concern, query, Patient_.firstname));
                break;
            default:
                return null;
        }

        dataAccessLogger.logPatientAccess(patients, concern, query);
        return patients;
    }

    @Override
    public Patient takeover(final int incidentId, final NotifyList notify) {
        Incident incident = incidentService.getById(incidentId);
        if (incident == null) {
            throw new ErrorsException(Errors.IncidentMissing);
        }

        if (incident.getConcern().isClosed()) {
            throw new ErrorsException(Errors.ConcernClosed);
        }

        if ((incident.getType() != IncidentType.Task && incident.getType() != IncidentType.Transport)
                || incident.getState().isDone() || !(incident.getAo() instanceof UnitPoint)) {
            throw new ErrorsException(Errors.IncidentNotAllowed);
        }

        if (incident.getPatient() != null) {
            if (incident.getPatient().isDone()) {
                throw new ErrorsException(Errors.PatientDone);
            }

            return incident.getPatient();
        }

        Patient patient = patientService.update(new Patient(), incident.getConcern(), notify);

        incident.setPatient(patient);
        incidentService.assignPatient(incident, patient, notify);

        return patient;
    }

    @Override
    public Patient update(final RegistrationForm form, final Concern concern, final NotifyList notify) {
        Patient old = form.getPatient() == null ? null : getActivePatient(form.getPatient());

        Patient patient = prepare(form, old);
        patient = patientService.update(patient, concern, notify);

        if (patient.getIncidents() != null) {
            patient.getIncidents().size();
        }

        if (form.getGroup() != null && (patient.getGroup() == null
                || patient.getGroup().stream().noneMatch(g -> g.getId().equals(form.getGroup())))) {
            incidentService.endTreatments(patient, notify);
            incidentService.createTreatment(patient, patadminService.getGroup(form.getGroup()), notify);
        }

        return patient;
    }

    private PatientSearchSpecification createSpecification(
            final Concern concern,
            final String query,
            final SingularAttribute<Patient, String> searchedField) {
        return new PatientSearchSpecification(query, concern, false) {
            @Override
            protected Predicate buildKeywordPredicate(String keyword, Root<Patient> root, CriteriaBuilder builder) {
                return builder.like(builder.lower(root.get(searchedField)), keyword + "%");
            }
        };
    }

    private Patient prepare(RegistrationForm form, Patient old) {
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

        return patient;
    }

}
