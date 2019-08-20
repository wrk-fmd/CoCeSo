package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.service.patadmin.InfoService;
import at.wrk.coceso.specification.PatientSearchSpecification;
import at.wrk.coceso.utils.DataAccessLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class InfoServiceImpl implements InfoService {

  @Autowired
  private PatientRepository patientRepository;

  private final DataAccessLogger dataAccessLogger;

  @Autowired
  InfoServiceImpl(final DataAccessLogger dataAccessLogger) {
    this.dataAccessLogger = dataAccessLogger;
  }

  @Override
  public Page<Patient> getAll(Concern concern, Pageable pageable) {
    Page<Patient> patients = patientRepository.findByConcern(concern, pageable);
    dataAccessLogger.logPatientAccess(patients.getContent(), concern);
    return patients;
  }

  @Override
  public Page<Patient> getByQuery(Concern concern, String query, Pageable pageable) {
    Page<Patient> patients = patientRepository.findAll(new PatientSearchSpecification(query, concern, true), pageable);
    dataAccessLogger.logPatientAccess(patients.getContent(), concern, query);
    return patients;
  }

}
