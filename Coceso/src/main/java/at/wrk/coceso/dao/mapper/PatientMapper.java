package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.entity.Patient;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PatientMapper implements RowMapper<Patient> {
    @Override
    public Patient mapRow(ResultSet rs, int i) throws SQLException {
        Patient p = new Patient();

        p.setIncident_id(rs.getInt("incident_fk"));
        p.setInfo(rs.getString("info"));
        p.setDiagnosis(rs.getString("diagnosis"));
        p.setErType(rs.getString("er_type"));
        p.setExternalID(rs.getString("external_id"));
        p.setGiven_name(rs.getString("given_name"));
        p.setSur_name(rs.getString("sur_name"));
        p.setInsurance_number(rs.getString("insurance_number"));

        return p;
    }
}
