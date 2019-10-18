package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface PostprocessingWriteService {

  Patient update(PostprocessingForm form);

  Patient discharge(PostprocessingForm form);

  Patient transported(int patientId);

  Patient transport(TransportForm form);

}
