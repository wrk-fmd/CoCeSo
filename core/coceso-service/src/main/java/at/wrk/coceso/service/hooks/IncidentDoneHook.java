package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;

interface IncidentDoneHook {

    void call(Incident incident);
}
