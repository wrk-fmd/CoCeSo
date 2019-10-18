package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.patadmin.internal.PostprocessingServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.patadmin.PostprocessingWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PostprocessingWriteServiceImpl implements PostprocessingWriteService {

  @Autowired
  private PostprocessingServiceInternal postprocessingService;

  @Autowired
  private EntityEventFactory entityEventFactory;

  @Override
  public Patient update(final PostprocessingForm form) {
    return NotifyList.execute(n -> postprocessingService.update(form, n), entityEventFactory);
  }

  @Override
  public Patient discharge(final PostprocessingForm form) {
    return NotifyList.execute(n -> postprocessingService.discharge(form, n), entityEventFactory);
  }

  @Override
  public Patient transported(final int patientId) {
    return NotifyList.execute(n -> postprocessingService.transported(patientId, n), entityEventFactory);
  }

  @Override
  public Patient transport(final TransportForm form) {
    return NotifyList.execute(n -> postprocessingService.transport(form, n), entityEventFactory);
  }

}
