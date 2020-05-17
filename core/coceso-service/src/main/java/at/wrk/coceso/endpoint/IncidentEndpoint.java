package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.incident.IncidentBriefDto;
import at.wrk.coceso.dto.incident.IncidentCreateDto;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.incident.IncidentUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.service.IncidentService;
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
@RequestMapping("/concerns/{concern}/incidents")
public class IncidentEndpoint {

    private final IncidentService incidentService;

    @Autowired
    public IncidentEndpoint(final IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).INCIDENT_READ)")
    @GetMapping
    public Collection<IncidentDto> getAllIncidents(@PathVariable final Concern concern) {
        ParamValidator.open(concern);
        return incidentService.getAllRelevant(concern);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).INCIDENT_EDIT)")
    @PostMapping
    public IncidentBriefDto createIncident(@PathVariable final Concern concern, @RequestBody @Valid final IncidentCreateDto data) {
        ParamValidator.open(concern);
        return incidentService.create(concern, data);
    }

    @PreAuthorize("hasPermission(#incident, T(at.wrk.coceso.auth.AccessLevel).INCIDENT_EDIT)")
    @PutMapping("/{incident}")
    public void updateIncident(@PathVariable final Concern concern, @PathVariable final Incident incident,
            @RequestBody @Valid final IncidentUpdateDto data) {
        ParamValidator.open(concern, incident);
        incidentService.update(incident, data);
    }

    @PreAuthorize("hasPermission(#incident, T(at.wrk.coceso.auth.AccessLevel).INCIDENT_EDIT)")
    @PutMapping("/{incident}/patients/{patient}")
    public void assignPatient(@PathVariable final Concern concern, @PathVariable final Incident incident,
            @PathVariable final Patient patient) {
        ParamValidator.open(concern, incident, patient);
        incidentService.assignPatient(incident, patient);
    }
}
