package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.service.hooks.HookService;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.repository.MedinfoRepository;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.utils.DataAccessLogger;
import java.util.List;
import java.util.Objects;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class PatientServiceImpl implements PatientService {

  private final static Logger LOG = LoggerFactory.getLogger(PatientServiceImpl.class);

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private MedinfoRepository medinfoRepository;

  @Autowired
  private LogService logService;

  @Autowired
  private HookService hookService;

  private final EntityEventHandler<Patient> patientEventHandler;
  private final EntityEventListener<Patient> entityEventListener;

  @Autowired
  public PatientServiceImpl(SocketMessagingTemplate template) {
    patientEventHandler = EntityEventHandler.getInstance(Patient.class);
    entityEventListener = patientEventHandler.addListener(new WebSocketWriter<>(template, "/topic/patient/main/%d", JsonViews.Main.class, null));
  }

  @PreDestroy
  public void destroy() {
    patientEventHandler.removeListener(entityEventListener);
  }

  @Override
  public List<Patient> getAll(Concern concern, User user) {
    List<Patient> patients = patientRepository.findByConcern(concern);
    DataAccessLogger.logPatientAccess(patients, concern, user);
    return patients;
  }

  @Override
  public Patient getByIdNoLog(int patientId) {
    Patient patient = patientRepository.findOne(patientId);
    if (patient == null) {
      throw new ErrorsException(Errors.HttpNotFound);
    }
    if (patient.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }
    return patient;
  }

  @Override
  public Patient getById(int patientId, User user) {
    Patient patient = getByIdNoLog(patientId);
    DataAccessLogger.logPatientAccess(patient, user);
    return patient;
  }

  @Override
  public Patient update(Patient patient, Concern concern, User user, NotifyList notify) {
    Changes changes = new Changes("patient");

    if (patient.getId() == null) {
      patient = prepareForCreate(patient, concern, changes, user);
      patient = patientRepository.save(patient);
      logService.logAuto(user, LogEntryType.PATIENT_CREATE, patient.getConcern(), patient, changes);
      notify.add(patient);
    } else {
      patient = prepareForUpdate(patient, changes, user);
      if (!changes.isEmpty()) {
        patient = patientRepository.save(patient);
        logService.logAuto(user, LogEntryType.PATIENT_UPDATE, patient.getConcern(), patient, changes);
        notify.add(patient);
      }
    }

    return patient;
  }

  @Override
  public Patient updateAndDischarge(Patient patient, User user, NotifyList notify) {
    Changes changes = new Changes("patient");

    if (patient.getId() == null) {
      throw new ErrorsException(Errors.PatientCreateNotAllowed);
    }

    patient = prepareForUpdate(patient, changes, user);

    if (!patient.isDone()) {
      changes.put("done", false, true);
      patient.setDone(true);
    }

    if (!changes.isEmpty()) {
      patient = patientRepository.save(patient);
      logService.logAuto(user, LogEntryType.PATIENT_UPDATE, patient.getConcern(), patient, changes);
      notify.add(patient);
    }

    hookService.callPatientDone(patient, user, notify);
    return patient;
  }

  @Override
  public Patient discharge(Patient patient, User user, NotifyList notify) {
    if (patient == null || patient.getId() == null) {
      return patient;
    }

    if (!patient.isDone()) {
      // Patient is not yet done: Set to done
      Changes changes = new Changes("patient");
      changes.put("done", false, true);
      patient.setDone(true);

      // Save and notify
      patient = patientRepository.save(patient);
      logService.logAuto(user, LogEntryType.PATIENT_UPDATE, patient.getConcern(), patient, changes);
      notify.add(patient);
    }

    hookService.callPatientDone(patient, user, notify);
    return patient;
  }

  private Patient prepareForCreate(Patient patient, Concern concern, Changes changes, User user) {
    LOG.info("{}: Triggered patient create", user);

    if (Concern.isClosed(concern)) {
      LOG.warn("{}: Tried to create patient without open concern", user);
      throw new ErrorsException(Errors.ConcernClosed);
    }

    Patient save = new Patient();

    // Set updated properties
    save.setConcern(concern);

    if (StringUtils.isNotBlank(patient.getLastname())) {
      changes.put("lastname", null, patient.getLastname());
      save.setLastname(patient.getLastname());
    }

    if (StringUtils.isNotBlank(patient.getFirstname())) {
      changes.put("firstname", null, patient.getFirstname());
      save.setFirstname(patient.getFirstname());
    }

    if (StringUtils.isNotBlank(patient.getExternalId())) {
      changes.put("externalId", null, patient.getExternalId());
      save.setExternalId(patient.getExternalId());
    }

    if (patient.getSex() != null) {
      changes.put("sex", null, patient.getSex());
      save.setSex(patient.getSex());
    }

    if (StringUtils.isNotBlank(patient.getInsurance())) {
      changes.put("insurance", null, patient.getInsurance());
      save.setInsurance(patient.getInsurance());
    }

    if (patient.getBirthday() != null) {
      changes.put("birthday", null, patient.getBirthday());
      save.setBirthday(patient.getBirthday());
    }

    if (patient.getNaca() != null) {
      changes.put("naca", null, patient.getNaca());
      save.setNaca(patient.getNaca());
    }

    if (StringUtils.isNotBlank(patient.getDiagnosis())) {
      changes.put("diagnosis", null, patient.getDiagnosis());
      save.setDiagnosis(patient.getDiagnosis());
    }

    if (StringUtils.isNotBlank(patient.getErtype())) {
      changes.put("ertype", null, patient.getErtype());
      save.setErtype(patient.getErtype());
    }

    if (StringUtils.isNotBlank(patient.getInfo())) {
      changes.put("info", null, patient.getInfo());
      save.setInfo(patient.getInfo());
    }

    if (patient.getMedinfo() != null) {
      Medinfo medinfo = medinfoRepository.findOne(patient.getMedinfo().getId());
      if (medinfo != null && medinfo.getConcern().equals(concern)) {
        changes.put("medinfo", null, medinfo.toString());
        save.setMedinfo(medinfo);
      }
    }

    return save;
  }

  private Patient prepareForUpdate(Patient patient, Changes changes, User user) {
    LOG.info("{}: Triggered update of patient #{}", user, patient.getId());

    Patient save = getByIdNoLog(patient.getId());

    if (save.getConcern().isClosed()) {
      LOG.warn("{}: Tried to update patient #{} in closed concern.", user, patient.getId());
      throw new ErrorsException(Errors.ConcernClosed);
    }

    // Set updateable properties
    if (!Objects.equals(save.getLastname(), patient.getLastname())) {
      changes.put("lastname", save.getLastname(), patient.getLastname());
      save.setLastname(patient.getLastname());
    }

    if (!Objects.equals(save.getFirstname(), patient.getFirstname())) {
      changes.put("firstname", save.getFirstname(), patient.getFirstname());
      save.setFirstname(patient.getFirstname());
    }

    if (!Objects.equals(save.getExternalId(), patient.getExternalId())) {
      changes.put("externalId", save.getExternalId(), patient.getExternalId());
      save.setExternalId(patient.getExternalId());
    }

    if (patient.getSex() != save.getSex()) {
      changes.put("sex", save.getSex(), patient.getSex());
      save.setSex(patient.getSex());
    }

    if (!Objects.equals(save.getInsurance(), patient.getInsurance())) {
      changes.put("insurance", save.getInsurance(), patient.getInsurance());
      save.setInsurance(patient.getInsurance());
    }

    if (!Objects.equals(save.getBirthday(), patient.getBirthday())) {
      changes.put("birthday", save.getBirthday(), patient.getBirthday());
      save.setBirthday(patient.getBirthday());
    }

    if (patient.getNaca() != save.getNaca()) {
      changes.put("naca", save.getNaca(), patient.getNaca());
      save.setNaca(patient.getNaca());
    }

    if (!Objects.equals(save.getDiagnosis(), patient.getDiagnosis())) {
      changes.put("diagnosis", save.getDiagnosis(), patient.getDiagnosis());
      save.setDiagnosis(patient.getDiagnosis());
    }

    if (!Objects.equals(save.getErtype(), patient.getErtype())) {
      changes.put("ertype", save.getErtype(), patient.getErtype());
      save.setErtype(patient.getErtype());
    }

    if (!Objects.equals(save.getInfo(), patient.getInfo())) {
      changes.put("info", save.getInfo(), patient.getInfo());
      save.setInfo(patient.getInfo());
    }

    if (patient.getMedinfo() != null) {
      Medinfo medinfo = medinfoRepository.findOne(patient.getMedinfo().getId());
      if (medinfo != null && medinfo.getConcern().equals(save.getConcern())) {
        changes.put("medinfo", save.getMedinfo() == null ? null : save.getMedinfo().toString(), medinfo.toString());
        save.setMedinfo(medinfo);
      }
    }

    return save;
  }

}
