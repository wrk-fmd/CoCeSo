package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Transactional
public interface UnitService {

  Unit getById(int id);

  List<Unit> getAll(Concern concern);

  List<Unit> getAllSorted(Concern concern);

  List<Unit> getByUser(User user, Collection<UnitType> types);

  List<Unit> getByConcernUser(Concern concern, int userId);

  Map<Unit, TaskState> getRelated(Incident incident);

}
