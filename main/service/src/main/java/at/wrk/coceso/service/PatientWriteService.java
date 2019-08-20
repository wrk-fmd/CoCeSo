package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface PatientWriteService {

  Patient update(Patient patient, Concern concern);

}
