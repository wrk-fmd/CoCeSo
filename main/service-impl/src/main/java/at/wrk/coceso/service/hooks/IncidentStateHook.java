package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entityevent.impl.NotifyList;

interface IncidentStateHook {

  public void call(Incident incident, IncidentState state, User user, NotifyList notify);
}
