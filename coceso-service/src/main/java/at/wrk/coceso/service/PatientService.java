package at.wrk.coceso.service;

import at.wrk.coceso.dto.patient.PatientBriefDto;
import at.wrk.coceso.dto.patient.PatientCreateDto;
import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.coceso.dto.patient.PatientUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;

import java.util.List;

public interface PatientService {

    List<PatientDto> getAll(Concern concern);

    List<Patient> getAllSorted(Concern concern);

    PatientBriefDto create(Concern concern, PatientCreateDto data);

    void update(Patient patient, PatientUpdateDto data);

    void discharge(Patient patient);
}
