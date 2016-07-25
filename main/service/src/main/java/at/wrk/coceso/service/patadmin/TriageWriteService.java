package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.form.TriageForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface TriageWriteService {

  Patient takeover(int incidentId, User user);

  Patient update(TriageForm form, Concern concern, User user);

}
