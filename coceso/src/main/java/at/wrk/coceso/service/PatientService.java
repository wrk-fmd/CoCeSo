package at.wrk.coceso.service;

import at.wrk.coceso.dao.PatientDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.LogEntryType;
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
        logService.logWithIDs(user.getId(), LogEntryType.PATIENT_CREATE, caseId, 0, patient.getId(), true);
        return ret;
    }

    public boolean update(Patient patient, Operator user, int caseId) {

        if(patientDao.update(patient)) {
            logService.logWithIDs(user.getId(), LogEntryType.PATIENT_UPDATE, caseId, 0, patient.getId(), true);
            return true;
        }
        return false;
    }

    public boolean remove(Patient patient) {
        return patientDao.remove(patient);
    }
}
