package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.impl.NotifyList;

interface IncidentDoneHook {

  public void call(Incident incident, User user, NotifyList notify);
}
