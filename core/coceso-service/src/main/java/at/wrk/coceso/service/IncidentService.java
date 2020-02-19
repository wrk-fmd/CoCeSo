package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface IncidentService {

  Incident getById(int id);

  List<Incident> getAll(Concern concern);

  List<Incident> getAllSorted(Concern concern);

  List<Incident> getAllRelevant(Concern concern);

  List<Incident> getAllForReport(Concern concern);

  List<Incident> getAllForDump(Concern concern);

  List<Incident> getAllTransports(Concern concern);

  List<Incident> getAllActive(Concern concern);

  Map<Incident, TaskState> getRelated(Unit unit);

}
