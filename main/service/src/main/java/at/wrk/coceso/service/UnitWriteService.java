package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.BatchUnits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface UnitWriteService {

  Unit updateMain(Unit unit, User user);

  Unit updateEdit(Unit unit, Concern concern, User user);

  List<Integer> batchCreate(BatchUnits batch, Concern concern, User user);

  void remove(int unitId, User user);

  void sendHome(int unitId, User user);

  void holdPosition(int unitId, User user);

  void standby(int unitId, User user);

  void removeCrew(int unitId, int user_id);

  void addCrew(int unitId, int user_id);

  int importUnits(String data, Concern concern, User user);

}
