package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface RegistrationService {

  List<Incident> getIncoming(Concern concern);

  List<Incident> getIncoming(Unit unit);

  Patient getActivePatient(int patientId, User user);

  List<Patient> getForAutocomplete(Concern concern, String query, String field, User user);

}
