package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.service.patadmin.RegistrationWriteService;
import at.wrk.coceso.service.patadmin.internal.RegistrationServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RegistrationWriteServiceImpl implements RegistrationWriteService {

  @Autowired
  private RegistrationServiceInternal registrationService;

  @Autowired
  private EntityEventFactory eef;

  @Override
  public Patient takeover(int incidentId, User user) {
    return NotifyList.execute(n -> registrationService.takeover(incidentId, user, n), eef);
  }

  @Override
  public Patient update(RegistrationForm form, Concern concern, User user) {
    return NotifyList.execute(n -> registrationService.update(form, concern, user, n), eef);
  }

}
