package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.BatchUnits;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface UnitSocketService {

  public Unit updateMain(Unit unit, User user);

  public Unit updateEdit(Unit unit, Concern concern, User user);

  public List<Integer> batchCreate(BatchUnits batch, Concern concern, User user);

  public void remove(int unitId, User user);

  public void sendHome(int unitId, User user);

  public void holdPosition(int unitId, User user);

  public void standby(int unitId, User user);

  public void removeCrew(int unit_id, int user_id);

  public void addCrew(int unit_id, int user_id);

  public int importUnits(String data, Concern concern, User user);

}
