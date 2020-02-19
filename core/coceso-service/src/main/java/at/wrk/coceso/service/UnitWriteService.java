package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.BatchUnits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface UnitWriteService {

  Unit updateMain(Unit unit);

  Unit updateEdit(Unit unit, Concern concern);

  List<Integer> batchCreate(BatchUnits batch, Concern concern);

  void remove(int unitId);

  void sendHome(int unitId);

  void holdPosition(int unitId);

  void standby(int unitId);

  void removeCrew(int unitId, int user_id);

  void addCrew(int unitId, int user_id);

  int importUnits(String data, Concern concern);

}
