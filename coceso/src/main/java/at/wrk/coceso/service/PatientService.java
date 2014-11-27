package at.wrk.coceso.service;

import at.wrk.coceso.dao.PatientDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.helper.JsonContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    @Autowired
    private PatientDao patientDao;

    @Autowired
    private LogService logService;

    public Patient getById(int id) {
        return patientDao.getById(id);
    }

    public Patient getByIncident(Incident incident) {
        if(incident == null || incident.getId() < 1)
            return null;
        return getById(incident.getId());
    }

    public List<Patient> getAll(int case_id) {
        return patientDao.getAll(case_id);
    }

    public int add(Patient patient, Operator user, int caseId) {
        int ret = patientDao.add(patient);
        if(ret <= 0) {
            return ret;
        }
        // TODO json
        logService.logAuto(user, LogEntryType.PATIENT_CREATE, caseId, null, new Incident(patient.getId()), (JsonContainer)null);
        return ret;
    }

    public boolean update(Patient patient, Operator user, int caseId) {

        if(patientDao.update(patient)) {
            // TODO json
            logService.logAuto(user, LogEntryType.PATIENT_UPDATE, caseId, null, new Incident(patient.getId()), (JsonContainer)null);
            return true;
        }
        return false;
    }

    public boolean remove(Patient patient) {
        return patientDao.remove(patient);
    }
}
