package at.wrk.coceso.service.patadmin.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.service.patadmin.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface RegistrationServiceInternal extends RegistrationService {

  Patient takeover(int incidentId, NotifyList notify);

  Patient update(RegistrationForm form, Concern concern, NotifyList notify);

}
