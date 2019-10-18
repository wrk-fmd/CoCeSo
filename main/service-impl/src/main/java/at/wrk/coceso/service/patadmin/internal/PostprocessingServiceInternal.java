package at.wrk.coceso.service.patadmin.internal;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.patadmin.PostprocessingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PostprocessingServiceInternal extends PostprocessingService {

  Patient update(PostprocessingForm form, NotifyList notify);

  Patient discharge(PostprocessingForm form, NotifyList notify);

  Patient transported(int patientId, NotifyList notify);

  Patient transport(TransportForm form, NotifyList notify);

}
