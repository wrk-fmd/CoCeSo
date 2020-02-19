package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface PatientService {

  List<Patient> getAll(Concern concern);

  List<Patient> getAllSorted(Concern concern);

  Patient getByIdNoLog(int patientId);

  Patient getById(int patientId);

}
