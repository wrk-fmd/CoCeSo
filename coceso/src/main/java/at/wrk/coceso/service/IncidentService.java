package at.wrk.coceso.service;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.ChangePair;
import at.wrk.coceso.entity.helper.JsonContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IncidentService {

    @Autowired
    private IncidentDao incidentDao;

    @Autowired
    private LogService logService;

    public Incident getById(int id) {
        return incidentDao.getById(id);
    }


    public List<Incident> getAll(int case_id) {
        return incidentDao.getAll(case_id);
    }

    public List<Incident> getAllRelevant(int case_id) {
        return incidentDao.getAllRelevant(case_id);
    }

    public List<Incident> getAllActive(int id) {
        return incidentDao.getAllActive(id);
    }

    public List<Incident> getAllByState(int id, IncidentState state) {
        return incidentDao.getAllByState(id, state);
    }

    public Map<Incident, TaskState> getRelated(int unit_id) {
      return incidentDao.getRelated(unit_id);
    }

    public boolean update(Incident incident) {
        return incidentDao.update(incident);
    }

    public boolean update(Incident incident, Operator operator) {
        Map<String, ChangePair<Object>> changes = incident.changes(getById(incident.getId()));
        boolean ret = update(incident);
        logService.logAuto(operator, LogEntryType.INCIDENT_UPDATE, incident.getConcern(), null, incident, new JsonContainer("incident", changes));
        return ret;
    }

    int add(Incident incident) {
        return incidentDao.add(incident);
    }

    public int add(Incident incident, Operator operator) {
        incident.setId(add(incident));
        logService.logAuto(operator, LogEntryType.INCIDENT_CREATE, incident.getConcern(), null, incident, new JsonContainer("incident", incident.changes(null)));
        return incident.getId();
    }

    boolean remove(Incident incident) {
        return incidentDao.remove(incident);
    }

    public boolean remove(Incident incident, Operator operator) {
        logService.logAuto(operator, LogEntryType.INCIDENT_DELETE, incident.getConcern(), null, incident, new JsonContainer("incident", incident.changes(null)));
        return remove(incident);
    }
}