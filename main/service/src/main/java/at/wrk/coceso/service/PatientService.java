package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.NotifyList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PatientService {

  List<Patient> getAll(Concern concern, User user);

  Patient getByIdNoLog(int patientId);

  Patient getById(int patientId, User user);

  Patient update(Patient patient, Concern concern, User user, NotifyList notify);

  Patient updateAndDischarge(Patient patient, User user, NotifyList notify);

  Patient discharge(Patient patient, User user, NotifyList notify);

}
