package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.patient.PatientBriefDto;
import at.wrk.coceso.dto.patient.PatientCreateDto;
import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.coceso.dto.patient.PatientUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/concerns/{concern}/patients")
public class PatientEndpoint {

    private final PatientService patientService;

    @Autowired
    public PatientEndpoint(final PatientService patientService) {
        this.patientService = patientService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).PATIENT_READ)")
    @GetMapping
    public Collection<PatientDto> getAllPatients(@PathVariable final Concern concern) {
        ParamValidator.open(concern);
        return patientService.getAll(concern);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).PATIENT_EDIT)")
    @PostMapping
    public PatientBriefDto createPatient(@PathVariable final Concern concern, @RequestBody @Valid final PatientCreateDto data) {
        ParamValidator.open(concern);
        return patientService.create(concern, data);
    }

    @PreAuthorize("hasPermission(#patient, T(at.wrk.coceso.auth.AccessLevel).PATIENT_EDIT)")
    @PutMapping("/{patient}")
    public void updatePatient(@PathVariable final Concern concern, @PathVariable final Patient patient,
            @RequestBody @Valid final PatientUpdateDto data) {
        ParamValidator.open(concern, patient);
        patientService.update(patient, data);
    }
}
