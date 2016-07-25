package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UnitService {

  public Unit getById(int id);

  public List<Unit> getAll(Concern concern);

  public List<Unit> getAllSorted(Concern concern);

  public List<Unit> getByUser(User user, Collection<UnitType> types);

  public List<Unit> getByConcernUser(Concern concern, User user);

  public Map<Unit, TaskState> getRelated(Incident incident);

}
