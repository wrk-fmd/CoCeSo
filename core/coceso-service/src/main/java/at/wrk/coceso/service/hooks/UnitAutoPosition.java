package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.JournalService;
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
    private JournalService journalService;

    @Override
    public TaskState call(final Incident incident, final Unit unit, final TaskState state) {
//    if (incident.getType() == IncidentType.Treatment) {
//      return state;
//    }

        Point position = null;
        if (state == TaskState.ABO) {
            position = incident.getBo();
        } else if (state == TaskState.AAO) {
            position = incident.getAo();
        }

        if (position == null) {
            return state;
        }

        LOG.debug("Position auto-set for unit {}", unit);

//    Changes changes = new Changes("unit");
//    changes.put("position", Point.toStringOrNull(unit.getPosition()), Point.toStringOrNull(position));
//    unit.setPosition(position);
//
//    unitRepository.saveAndFlush(unit);
//    logService.logAuto(LogEntryType.UNIT_AUTOSET_POSITION, unit.getConcern(), unit, incident, state, changes);
//    notify.addUnit(unit.getId());
        return state;
    }
}
