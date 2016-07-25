package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.internal.IncidentServiceInternal;
import at.wrk.coceso.service.internal.PatientServiceInternal;
import at.wrk.coceso.service.patadmin.internal.PostprocessingServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class PostprocessingServiceImpl implements PostprocessingServiceInternal {

  @Autowired
  private PatientServiceInternal patientService;

  @Autowired
  private IncidentServiceInternal incidentService;

  private Patient checkActive(Patient patient) {
    if (patient.isDone() || patient.isTransport()) {
      throw new ErrorsException(Errors.PatientDone);
    }
    return patient;
  }

  private Patient checkTransported(Patient patient) {
    if (patient.isDone() || !patient.isTransport()) {
      throw new ErrorsException(Errors.PatientDone);
    }
    return patient;
  }

  private Patient getActivePatientNoLog(int patientId) {
    return checkActive(patientService.getByIdNoLog(patientId));
  }

  @Override
  public Patient getActivePatient(int patientId, User user) {
    return checkActive(patientService.getById(patientId, user));
  }

  @Override
  public Patient getTransported(int patientId, User user) {
    return checkTransported(patientService.getById(patientId, user));
  }

  @Override
  public Patient update(PostprocessingForm form, User user, NotifyList notify) {
    Patient patient = prepareForUpdate(form, patientService.getByIdNoLog(form.getPatient()));
    return patientService.update(patient, patient.getConcern(), user, notify);
  }

  @Override
  public Patient discharge(PostprocessingForm form, User user, NotifyList notify) {
    Patient patient = prepareForUpdate(form, getActivePatientNoLog(form.getPatient()));
    return patientService.updateAndDischarge(patient, user, notify);
  }

  @Override
  public Patient transported(int patientId, User user, NotifyList notify) {
    Patient patient = checkTransported(patientService.getByIdNoLog(patientId));
    incidentService.endTreatments(patient, user, notify);
    return patient;
  }

  @Override
  public Patient transport(TransportForm form, User user, NotifyList notify) {
    Patient patient = prepareForUpdate(form, getActivePatientNoLog(form.getPatient()));
    patient.setErtype(form.getErtype());
    patient = patientService.update(patient, patient.getConcern(), user, notify);

    Incident incident = incidentService.update(createTransport(patient, form), patient.getConcern(), user, notify);
    incidentService.assignPatient(incident, patient, user, notify);

    return patient;
  }

  private Patient prepareForUpdate(PostprocessingForm form, Patient old) {
    Patient p = new Patient();

    p.setErtype(old.getErtype());

    p.setId(form.getPatient());
    p.setLastname(form.getLastname());
    p.setFirstname(form.getFirstname());
    p.setExternalId(form.getExternalId());
    p.setSex(form.getSex());
    p.setInsurance(form.getInsurance());
    p.setBirthday(form.getBirthday());
    p.setNaca(form.getNaca());
    p.setDiagnosis(form.getDiagnosis());
    p.setInfo(form.getInfo());
    return p;
  }

  private Incident createTransport(Patient patient, TransportForm form) {
    Incident incident = new Incident();

    incident.setState(IncidentState.New);
    incident.setType(IncidentType.Transport);
    incident.setBlue(true);

    if (form.getAmbulance() != null) {
      incident.setInfo(form.getAmbulance().name());
    }

    incident.setPriority(form.isPriority());

    Unit group = patient.getGroup().stream().findFirst().orElse(null);
    if (group != null) {
      incident.setBo(new Point(group.getCall()));

      if (group.getSection() != null) {
        incident.setSection(group.getSection());
      }
    }

    return incident;
  }

}
