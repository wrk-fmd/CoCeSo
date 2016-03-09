package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.form.TriageForm;
import at.wrk.coceso.service.patadmin.TriageService;
import at.wrk.coceso.service.patadmin.TriageSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class TriageSocketServiceImpl implements TriageSocketService {

  @Autowired
  private TriageService triageService;

  @Override
  public Patient takeover(int incidentId, User user) {
    return NotifyList.execute(n -> triageService.takeover(incidentId, user, n));
  }

  @Override
  public Patient update(TriageForm form, Concern concern, User user) {
    return NotifyList.execute(n -> triageService.update(form, concern, user, n));
  }

}
