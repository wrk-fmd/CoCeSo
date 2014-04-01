package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.PatientMapper;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientDao extends CocesoDao<Patient> {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    public PatientDao(DataSource dataSource) {
        super(dataSource);
    }

    // Same ID as connected Incident
    @Override
    public Patient getById(int id) {
        if(id < 1) {
            Logger.error("PatientDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = "SELECT * FROM patient WHERE incident_fk = ?";
        Patient patient;

        try {
            patient = jdbc.queryForObject(q, new Object[] {id}, patientMapper);
        }
        catch(IncorrectResultSizeDataAccessException e) {
            Logger.debug("PatientDao.getById(int): requested id: " + id + "; "+e.getMessage());
            return null;
        }
        catch(DataAccessException dae) {
            Logger.warning("PatientDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
            return null;
        }

        return patient;
    }

    @Override
    public List<Patient> getAll(int case_id) {
        if(case_id <= 0) {
            return null;
        }

        String q = "SELECT * FROM patient p, incident i WHERE p.incident_fk = i.id AND i.concern_fk = ?";

        return jdbc.query(q, new Object[] {case_id}, patientMapper);
    }

    @Override
    public boolean update(Patient patient) {
        if(patient == null) return false;

        final String pre_q = "UPDATE patient SET ";
        final String suf_q = "WHERE incident_fk = ?";

        String q = pre_q;
        List<Object> parameters = new ArrayList<Object>();
        boolean comma = false;

        if(patient.getInfo() != null) {
            q += "info = ? ";
            parameters.add(patient.getInfo());
            comma = true;
        }
        if(patient.getSur_name() != null) {
            if(comma) q+= ",";
            q += "sur_name = ? ";
            parameters.add(patient.getSur_name());
            comma = true;
        }
        if(patient.getGiven_name() != null) {
            if(comma) q+= ",";
            q += "given_name = ? ";
            parameters.add(patient.getGiven_name());
            comma = true;
        }
        if(patient.getDiagnosis() != null) {
            if(comma) q+= ",";
            q += "diagnosis = ? ";
            parameters.add(patient.getDiagnosis());
            comma = true;
        }
        if(patient.getErType() != null) {
            if(comma) q+= ",";
            q += "er_type = ? ";
            parameters.add(patient.getErType());
            comma = true;
        }
        if(patient.getInsurance_number() != null) {
            if(comma) q+= ",";
            q += "insurance_number = ? ";
            parameters.add(patient.getInsurance_number());
            comma = true;
        }
        if(patient.getSex() != null) {
            if(comma) q+= ",";
            q += "sex = ? ";
            parameters.add(patient.getSex());
            comma = true;
        }
        if(patient.getExternalID() != null) {
            if(comma) q+= ",";
            q += "external_id = ? ";
            parameters.add(patient.getExternalID());
            // comma = true;
        }

        parameters.add(patient.getId());

        q += suf_q;

        try {
            jdbc.update(q, parameters.toArray());
        } catch (DataAccessException dae) {
            Logger.warning(dae.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public int add(Patient patient) {
        if(patient == null || patient.getId() < 1) {
            Logger.debug("PatientDao.add(): Invalid Parameter patient");
            return -1;
        }
        String q = "INSERT INTO patient (incident_fk, info, given_name, sur_name, external_id, " +
                "er_type, diagnosis, insurance_number, sex) VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            jdbc.update(q,
                    patient.getId(),
                    patient.getInfo(),
                    patient.getGiven_name(),
                    patient.getSur_name(),
                    patient.getExternalID(),
                    patient.getErType(),
                    patient.getDiagnosis(),
                    patient.getInsurance_number(),
                    patient.getSex());
        } catch(DataAccessException e) {
            Logger.debug("TaskDao add: "+e);
            return -1;
        }
        return patient.getId();
    }

    @Override
    public boolean remove(Patient patient) {
        if(patient == null) return false;
        String q = "DELETE FROM patient WHERE incident_fk = ?";

        try {
            jdbc.update(q, patient.getId());
        } catch (DataAccessException dae) {
            Logger.warning("PatientDao.remove(): "+ dae.getMessage());
            return false;
        }

        return true;
    }
}
