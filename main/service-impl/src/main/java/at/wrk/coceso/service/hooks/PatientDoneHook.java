package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.impl.NotifyList;

interface PatientDoneHook {

  public void call(Patient patient, User user, NotifyList notify);
}
