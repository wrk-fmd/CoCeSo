package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
@Transactional
class UnitAutoPosition implements TaskStateHook {

  private static final Logger LOG = LoggerFactory.getLogger(UnitAutoPosition.class);

  @Autowired
  private UnitRepository unitRepository;

  @Autowired
  private LogService logService;

  @Override
  public TaskState call(final Incident incident, final Unit unit, final TaskState state, final User user, final NotifyList notify) {
    if (incident.getType() == IncidentType.Treatment) {
      return state;
    }

    Point position = null;
    if (state == TaskState.ABO) {
      position = incident.getBo();
    } else if (state == TaskState.AAO) {
      position = incident.getAo();
    }

    if (position == null) {
      return state;
    }

    LOG.debug("{}: Position autoset for unit {}", user, unit);

    Changes changes = new Changes("unit");
    changes.put("position", Point.toStringOrNull(unit.getPosition()), Point.toStringOrNull(position));
    unit.setPosition(position);

    unitRepository.saveAndFlush(unit);
    logService.logAuto(user, LogEntryType.UNIT_AUTOSET_POSITION, unit.getConcern(), unit, incident, state, changes);
    notify.add(unit);

    return state;
  }

}
