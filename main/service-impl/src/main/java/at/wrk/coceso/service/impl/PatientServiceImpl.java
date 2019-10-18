package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.hooks.HookService;
import at.wrk.coceso.service.internal.PatientServiceInternal;
import at.wrk.coceso.utils.AuthenicatedUserProvider;
import at.wrk.coceso.utils.DataAccessLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
class PatientServiceImpl implements PatientServiceInternal {

  private final static Logger LOG = LoggerFactory.getLogger(PatientServiceImpl.class);

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private LogService logService;

  @Autowired
  private HookService hookService;

  private final EntityEventHandler<Patient> patientEventHandler;
  private final EntityEventListener<Patient> entityEventListener;
  private final DataAccessLogger dataAccessLogger;
  private final AuthenicatedUserProvider authenicatedUserProvider;

  @Autowired
  public PatientServiceImpl(
          final EntityEventFactory entityEventFactory,
          final DataAccessLogger dataAccessLogger,
          final AuthenicatedUserProvider authenicatedUserProvider) {
    patientEventHandler = entityEventFactory.getEntityEventHandler(Patient.class);
    entityEventListener = entityEventFactory.getWebSocketWriter("/topic/patient/main/%d", JsonViews.Main.class, null);
    this.dataAccessLogger = dataAccessLogger;
    this.authenicatedUserProvider = authenicatedUserProvider;
    patientEventHandler.addListener(entityEventListener);
  }

  @PreDestroy
  public void destroy() {
    patientEventHandler.removeListener(entityEventListener);
  }

  @Override
  public List<Patient> getAll(final Concern concern) {
    List<Patient> patients = patientRepository.findByConcern(concern);
    dataAccessLogger.logPatientAccess(patients, concern);
    return patients;
  }

  @Override
  public List<Patient> getAllSorted(final Concern concern) {
    List<Patient> patients = patientRepository.findByConcern(concern, new Sort(Sort.Direction.ASC, "id"));
    dataAccessLogger.logPatientAccess(patients, concern);
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
  public Patient getById(int patientId) {
    Patient patient = getByIdNoLog(patientId);
    dataAccessLogger.logPatientAccess(patient);
    return patient;
  }

  @Override
  public Patient update(Patient patient, Concern concern, NotifyList notify) {
    Changes changes = new Changes("patient");

    if (patient.getId() == null) {
      patient = prepareForCreate(patient, concern, changes);
      patient = patientRepository.saveAndFlush(patient);
      logService.logAuto(LogEntryType.PATIENT_CREATE, patient.getConcern(), patient, changes);
      notify.add(patient);
    } else {
      patient = prepareForUpdate(patient, changes);
      if (!changes.isEmpty()) {
        patient = patientRepository.saveAndFlush(patient);
        logService.logAuto(LogEntryType.PATIENT_UPDATE, patient.getConcern(), patient, changes);
        notify.add(patient);
      }
    }

    return patient;
  }

  @Override
  public Patient updateAndDischarge(Patient patient, final NotifyList notify) {
    Changes changes = new Changes("patient");

    if (patient.getId() == null) {
      throw new ErrorsException(Errors.PatientCreateNotAllowed);
    }

    patient = prepareForUpdate(patient, changes);

    if (!patient.isDone()) {
      changes.put("done", false, true);
      patient.setDone(true);
    }

    if (!changes.isEmpty()) {
      patient = patientRepository.saveAndFlush(patient);
      logService.logAuto(LogEntryType.PATIENT_UPDATE, patient.getConcern(), patient, changes);
      notify.add(patient);
    }

    hookService.callPatientDone(patient, notify);
    return patient;
  }

  @Override
  public Patient discharge(Patient patient, NotifyList notify) {
    if (patient == null || patient.getId() == null) {
      return patient;
    }

    if (!patient.isDone()) {
      // Patient is not yet done: Set to done
      Changes changes = new Changes("patient");
      changes.put("done", false, true);
      patient.setDone(true);

      // Save and notify
      patient = patientRepository.saveAndFlush(patient);
      logService.logAuto(LogEntryType.PATIENT_UPDATE, patient.getConcern(), patient, changes);
      notify.add(patient);
    }

    hookService.callPatientDone(patient, notify);
    return patient;
  }

  private Patient prepareForCreate(final Patient patient, final Concern concern, final Changes changes) {
    LOG.debug("{}: Creating patient: {}", authenicatedUserProvider.getAuthenticatedUser(), patient);

    if (Concern.isClosed(concern)) {
      LOG.warn("Patient cannot be created without open concern!");
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
      changes.put("birthday", null, patient.getBirthday().format(DateTimeFormatter.ISO_DATE));
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

    return save;
  }

  private Patient prepareForUpdate(final Patient patient, final Changes changes) {
    LOG.debug("{}: Updating patient #{}", authenicatedUserProvider.getAuthenticatedUser(), patient.getId());

    Patient save = getByIdNoLog(patient.getId());

    if (save.getConcern().isClosed()) {
      LOG.warn("Tried to update patient #{} in closed concern.", patient.getId());
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
      changes.put("birthday", save.getBirthday() == null ? null : save.getBirthday().format(DateTimeFormatter.ISO_DATE),
          patient.getBirthday() == null ? null : patient.getBirthday().format(DateTimeFormatter.ISO_DATE));
      save.setBirthday(patient.getBirthday());
    }

    if (patient.getNaca() != null && patient.getNaca() != save.getNaca()) {
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

    return save;
  }

}
