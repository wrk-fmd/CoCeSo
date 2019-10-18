package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entityevent.impl.NotifyList;

interface IncidentDoneHook {

  void call(Incident incident, NotifyList notify);
}
