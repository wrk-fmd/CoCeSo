package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.patadmin.PostprocessingService;
import at.wrk.coceso.service.patadmin.PostprocessingSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PostprocessingSocketServiceImpl implements PostprocessingSocketService {

  @Autowired
  private PostprocessingService postprocessingService;

  @Override
  public Patient update(PostprocessingForm form, User user) {
    return NotifyList.execute(n -> postprocessingService.update(form, user, n));
  }

  @Override
  public Patient discharge(PostprocessingForm form, User user) {
    return NotifyList.execute(n -> postprocessingService.discharge(form, user, n));
  }

  @Override
  public Patient transported(int patientId, User user) {
    return NotifyList.execute(n -> postprocessingService.transported(patientId, user, n));
  }

  @Override
  public Patient transport(TransportForm form, User user) {
    return NotifyList.execute(n -> postprocessingService.transport(form, user, n));
  }

}
