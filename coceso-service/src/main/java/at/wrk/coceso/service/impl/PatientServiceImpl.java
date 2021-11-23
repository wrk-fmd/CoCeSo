package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.patient.PatientBriefDto;
import at.wrk.coceso.dto.patient.PatientCreateDto;
import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.coceso.dto.patient.PatientUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.Sex;
import at.wrk.coceso.entity.journal.ChangesCollector;
import at.wrk.coceso.event.events.PatientEvent;
import at.wrk.coceso.mapper.PatientMapper;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.service.LoggingService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.utils.AuthenticatedUser;
import at.wrk.fmd.mls.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final JournalService journalService;
    private final LoggingService accessLoggingService;
    private final EventBus eventBus;

    @Autowired
    public PatientServiceImpl(final PatientRepository patientRepository, final PatientMapper patientMapper,
            final JournalService journalService,
            final LoggingService accessLoggingService, final EventBus eventBus) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.journalService = journalService;
        this.accessLoggingService = accessLoggingService;
        this.eventBus = eventBus;
    }

    @Override
    public List<PatientDto> getAll(final Concern concern) {
        List<Patient> patients = patientRepository.findByConcern(concern);
        accessLoggingService.logPatientAccess(patients, concern);
        return patients.stream()
                .map(patientMapper::patientToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> getAllSorted(final Concern concern) {
        List<Patient> patients = patientRepository.findByConcern(concern, Sort.by(Sort.Direction.ASC, "id"));
        accessLoggingService.logPatientAccess(patients, concern);
        return patients;
    }

    @Override
    public PatientBriefDto create(final Concern concern, final PatientCreateDto data) {
        log.debug("{}: Creating patient: '{}'", AuthenticatedUser.getName(), data);

        Patient patient = new Patient();
        ChangesCollector changes = new ChangesCollector("patient");

        // Set properties
        patient.setConcern(concern);

        if (data.getLastname() != null && !data.getLastname().isEmpty()) {
            changes.put("lastname", data.getLastname());
            patient.setLastname(data.getLastname());
        }

        if (data.getFirstname() != null && !data.getFirstname().isEmpty()) {
            changes.put("firstname", data.getFirstname());
            patient.setFirstname(data.getFirstname());
        }

        if (data.getExternalId() != null && !data.getExternalId().isEmpty()) {
            changes.put("externalId", data.getExternalId());
            patient.setExternalId(data.getExternalId());
        }

        if (data.getSex() != null) {
            Sex sex = patientMapper.sexDtoToSex(data.getSex());
            changes.put("sex", sex);
            patient.setSex(sex);
        }

        if (data.getInsurance() != null && !data.getInsurance().isEmpty()) {
            changes.put("insurance", data.getInsurance());
            patient.setInsurance(data.getInsurance());
        }

        if (data.getBirthday() != null) {
            changes.put("birthday", patientMapper.dateToString(data.getBirthday()));
            patient.setBirthday(data.getBirthday());
        }

        if (data.getDiagnosis() != null && !data.getDiagnosis().isEmpty()) {
            changes.put("diagnosis", data.getDiagnosis());
            patient.setDiagnosis(data.getDiagnosis());
        }

        if (data.getErType() != null && !data.getErType().isEmpty()) {
            changes.put("erType", data.getErType());
            patient.setErType(data.getErType());
        }

        if (data.getInfo() != null && !data.getInfo().isEmpty()) {
            changes.put("info", data.getInfo());
            patient.setInfo(data.getInfo());
        }

        patient = patientRepository.save(patient);
        journalService.logPatient(JournalEntryType.PATIENT_CREATE, patient, changes);
        eventBus.publish(new PatientEvent(patientMapper.patientToDto(patient)));

        return patientMapper.patientToBriefDto(patient);
    }

    @Override
    public void update(final Patient patient, final PatientUpdateDto data) {
        log.debug("{}: Updating patient '{}': '{}'", AuthenticatedUser.getName(), patient, data);

        ChangesCollector changes = new ChangesCollector("patient");

        // Set updateable properties
        if (data.getLastname() != null && !data.getLastname().equals(patient.getLastname())) {
            changes.put("lastname", patient.getLastname(), data.getLastname());
            patient.setLastname(data.getLastname());
        }

        if (data.getFirstname() != null && !data.getFirstname().equals(patient.getFirstname())) {
            changes.put("firstname", patient.getFirstname(), data.getFirstname());
            patient.setFirstname(data.getFirstname());
        }

        if (data.getExternalId() != null && !data.getExternalId().equals(patient.getExternalId())) {
            changes.put("externalId", patient.getExternalId(), data.getExternalId());
            patient.setExternalId(data.getExternalId());
        }

        Sex sex = patientMapper.sexDtoToSex(data.getSex());
        if (sex != null && sex != patient.getSex()) {
            changes.put("sex", patient.getSex(), sex);
            patient.setSex(sex);
        }

        if (data.getInsurance() != null && !data.getInsurance().equals(patient.getInsurance())) {
            changes.put("insurance", patient.getInsurance(), data.getInsurance());
            patient.setInsurance(data.getInsurance());
        }

        if (data.getBirthday() != null && !data.getBirthday().equals(patient.getBirthday())) {
            changes.put("birthday", patientMapper.dateToString(patient.getBirthday()), patientMapper.dateToString(data.getBirthday()));
            patient.setBirthday(data.getBirthday());
        }

        if (data.getDiagnosis() != null && !data.getDiagnosis().equals(patient.getDiagnosis())) {
            changes.put("diagnosis", patient.getDiagnosis(), data.getDiagnosis());
            patient.setDiagnosis(data.getDiagnosis());
        }

        if (data.getErType() != null && !data.getErType().equals(patient.getErType())) {
            changes.put("erType", patient.getErType(), data.getErType());
            patient.setErType(data.getErType());
        }

        if (data.getInfo() != null && !data.getInfo().equals(patient.getInfo())) {
            changes.put("info", patient.getInfo(), data.getInfo());
            patient.setInfo(data.getInfo());
        }

        if (!changes.isEmpty()) {
            journalService.logPatient(JournalEntryType.PATIENT_UPDATE, patient, changes);
            eventBus.publish(new PatientEvent(patientMapper.patientToDto(patient)));
        }
    }

    @Override
    public void discharge(Patient patient) {
        if (patient == null || patient.getId() == null) {
            return;
        }

        if (!patient.isDone()) {
            // Patient is not yet done: Set to done
            ChangesCollector changes = new ChangesCollector("patient");
            changes.put("done", false, true);
            patient.setDone(true);

            // Save and notify
            patient = patientRepository.saveAndFlush(patient);
            journalService.logPatient(JournalEntryType.PATIENT_UPDATE, patient, changes);
        }

        // TODO
        //hookService.callPatientDone(patient, notify);
    }
}
