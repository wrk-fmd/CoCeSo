package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.form.RegistrationForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface RegistrationWriteService {

  Patient takeover(int incidentId);

  Patient update(RegistrationForm form, Concern concern);

}
