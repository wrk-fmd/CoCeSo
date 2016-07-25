package at.wrk.coceso.service.patadmin.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.TriageForm;
import at.wrk.coceso.service.patadmin.TriageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface TriageServiceInternal extends TriageService {

  Patient takeover(int incidentId, User user, NotifyList notify);

  Patient update(TriageForm form, Concern concern, User user, NotifyList notify);

}
