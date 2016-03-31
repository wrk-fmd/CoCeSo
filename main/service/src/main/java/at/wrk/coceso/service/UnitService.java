package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entityevent.NotifyList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UnitService {

  public Unit getById(int id);

  public List<Unit> getAll(Concern concern);

  public List<Unit> getByUser(User user, Collection<UnitType> types);

  public List<Unit> getByConcernUser(Concern concern, User user);

  public Map<Unit, TaskState> getRelated(Incident incident);

  public Unit updateMain(Unit unit, User user, NotifyList notify);

  public Unit updateEdit(Unit unit, Concern concern, User user, NotifyList notify);

  public List<Integer> batchCreate(BatchUnits batch, Concern concern, User user, NotifyList notify);

  public Unit doRemove(int unitId, User user);

  public void sendHome(int unitId, User user, NotifyList notify);

  public void holdPosition(int unitId, User user, NotifyList notify);

  public void standby(int unitId, User user, NotifyList notify);

  public void removeCrew(int unit_id, int user_id, NotifyList notify);

  public void addCrew(int unit_id, int user_id, NotifyList notify);

  public int importUnits(String data, Concern concern, User user, NotifyList notify);

}
