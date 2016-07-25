package at.wrk.coceso.service.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PatientServiceInternal extends PatientService {

  Patient update(Patient patient, Concern concern, User user, NotifyList notify);

  Patient updateAndDischarge(Patient patient, User user, NotifyList notify);

  Patient discharge(Patient patient, User user, NotifyList notify);

}
