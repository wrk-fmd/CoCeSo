package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.impl.NotifyList;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class IncidentSetEnded implements IncidentDoneHook {

  @Override
  public void call(Incident incident, User user, NotifyList notify) {
    incident.setEnded();
  }

}
