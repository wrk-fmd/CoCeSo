package at.wrk.coceso.service.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.UnitService;

import java.util.List;

public interface UnitServiceInternal extends UnitService {

    Unit updateMain(Unit unit, NotifyList notify);

    Unit updateEdit(Unit unit, Concern concern, NotifyList notify);

    List<Integer> batchCreate(BatchUnits batch, Concern concern, NotifyList notify);

    Unit doRemove(int unitId);

    void sendHome(int unitId, NotifyList notify);

    void holdPosition(int unitId, NotifyList notify);

    void standby(int unitId, NotifyList notify);

    void removeCrew(int unitId, int userId, NotifyList notify);

    void addCrew(int unitId, int userId, NotifyList notify);

    int importUnits(String data, Concern concern, NotifyList notify);

}
