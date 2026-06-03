package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/data/patadmin", method = RequestMethod.GET)
public class PatadminRestController {

    private final PatadminService patadminService;
    private final IncidentService incidentService;
    private final UnitService unitService;
    private final EntityEventHandler<Patient> patientEventHandler;
    private final EntityEventHandler<Incident> incidentEventHandler;

    public PatadminRestController(
            final PatadminService patadminService,
            final IncidentService incidentService,
            final UnitService unitService,
            final EntityEventFactory entityEventFactory) {
        this.patadminService = patadminService;
        this.incidentService = incidentService;
        this.unitService = unitService;
        this.patientEventHandler = entityEventFactory.getEntityEventHandler(Patient.class);
        this.incidentEventHandler = entityEventFactory.getEntityEventHandler(Incident.class);
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'Patadmin')")
    @Transactional
    @JsonView(JsonViews.Main.class)
    @RequestMapping(value = "patients", produces = "application/json")
    public SequencedResponse<List<Patient>> getPatients(@ActiveConcern final Concern concern) {
        Set<Patient> merged = new LinkedHashSet<>(patadminService.getAllInTreatment(concern));
        merged.addAll(patadminService.getAllInTransport(concern));
        List<Patient> patients = Initializer.initGroups(new ArrayList<>(merged));
        return new SequencedResponse<>(
                patientEventHandler.getHver(),
                patientEventHandler.getSeq(concern.getId()),
                patients);
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'Patadmin')")
    @Transactional
    @JsonView(JsonViews.Main.class)
    @RequestMapping(value = "incidents", produces = "application/json")
    public SequencedResponse<List<Incident>> getIncidents(@ActiveConcern final Concern concern) {
        List<Incident> incidents = Initializer.init(
                incidentService.getAllActive(concern),
                Incident::getUnits,
                Incident::getPatient);
        return new SequencedResponse<>(
                incidentEventHandler.getHver(),
                incidentEventHandler.getSeq(concern.getId()),
                incidents);
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'Patadmin')")
    @Transactional
    @JsonView(JsonViews.Main.class)
    @RequestMapping(value = "unit", produces = "application/json")
    public Unit getUnit(
            @RequestParam("id") final int id,
            @ActiveConcern final Concern concern) {
        Unit unit = unitService.getById(id);
        if (unit == null || !concern.equals(unit.getConcern())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Initializer.init(unit, Unit::getIncidents, Unit::getIncidentStateChangedAtMap);
        return unit;
    }
}
