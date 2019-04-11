package at.wrk.coceso.service.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.UnitService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UnitServiceInternal extends UnitService {

    Unit updateMain(Unit unit, User user, NotifyList notify);

    Unit updateEdit(Unit unit, Concern concern, User user, NotifyList notify);

    List<Integer> batchCreate(BatchUnits batch, Concern concern, User user, NotifyList notify);

    Unit doRemove(int unitId, User user);

    void sendHome(int unitId, User user, NotifyList notify);

    void holdPosition(int unitId, User user, NotifyList notify);

    void standby(int unitId, User user, NotifyList notify);

    void removeCrew(int unit_id, int user_id, NotifyList notify);

    void addCrew(int unit_id, int user_id, NotifyList notify);

    int importUnits(String data, Concern concern, User user, NotifyList notify);

}
