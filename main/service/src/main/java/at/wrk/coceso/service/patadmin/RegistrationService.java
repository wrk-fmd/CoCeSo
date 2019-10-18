package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface RegistrationService {

  List<Incident> getIncoming(Concern concern);

  List<Incident> getIncoming(Unit unit);

  long getTreatmentCount(Concern concern);
  
  long getTransportCount(Concern concern);
  
  Patient getActivePatient(int patientId);

  List<Patient> getForAutocomplete(Concern concern, String query, String field);

}
