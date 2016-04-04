package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PostprocessingService {

  Patient getActivePatient(int patientId, User user);

  Patient getTransported(int patientId, User user);

  Patient update(PostprocessingForm form, User user, NotifyList notify);

  Patient discharge(PostprocessingForm form, User user, NotifyList notify);

  Patient transported(int patientId, User user, NotifyList notify);

  Patient transport(TransportForm form, User user, NotifyList notify);

}
