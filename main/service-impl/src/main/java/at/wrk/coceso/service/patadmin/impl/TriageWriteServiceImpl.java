package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.TriageForm;
import at.wrk.coceso.service.patadmin.internal.TriageServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.patadmin.TriageWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TriageWriteServiceImpl implements TriageWriteService {

  @Autowired
  private TriageServiceInternal triageService;

  @Autowired
  private EntityEventFactory eef;

  @Override
  public Patient takeover(int incidentId, User user) {
    return NotifyList.execute(n -> triageService.takeover(incidentId, user, n), eef);
  }

  @Override
  public Patient update(TriageForm form, Concern concern, User user) {
    return NotifyList.execute(n -> triageService.update(form, concern, user, n), eef);
  }

}
