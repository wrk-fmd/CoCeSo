package at.wrk.coceso.service;

import at.wrk.coceso.dao.CocesoDao;
import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.entities.Incident;
import at.wrk.coceso.entities.IncidentState;
import at.wrk.coceso.entities.Operator;
import at.wrk.coceso.utils.LogText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    @Autowired
    IncidentDao incidentDao;

    @Autowired
    LogService logService;

    public Incident getById(int id) {
        return incidentDao.getById(id);
    }


    public List<Incident> getAll(int case_id) {
        return incidentDao.getAll(case_id);
    }

    public List<Incident> getAllActive(int id) {
        return incidentDao.getAllActive(id);
    }

    public List<Incident> getAllByState(int id, IncidentState state) {
        return incidentDao.getAllByState(id, state);
    }

    public boolean update(Incident incident) {
        return incidentDao.update(incident);
    }

    public boolean update(Incident incident, Operator operator) {
        boolean ret = update(incident);
        logService.logFull(operator, LogText.INCIDENT_UPDATE, incident.concern, null, incident, true);
        return ret;
    }

    public int add(Incident incident) {
        return incidentDao.add(incident);
    }

    public int add(Incident incident, Operator operator) {
        int ret = add(incident);
        logService.logFull(operator, LogText.INCIDENT_NEW, incident.concern, null, incident, true);
        return ret;
    }

    public boolean remove(Incident incident) {
        return incidentDao.remove(incident);
    }

    public boolean remove(Incident incident, Operator operator) {
        logService.logFull(operator, LogText.INCIDENT_DELETE, incident.concern, null, incident, true);
        return remove(incident);
    }
}
