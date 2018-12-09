package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.IncidentWriteService;
import at.wrk.coceso.service.TaskWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("@auth.hasAccessLevel('Main')")
@RestController
@RequestMapping("/data/incident")
public class IncidentController {

    private final EntityEventHandler<Incident> entityEventHandler;
    private final IncidentService incidentService;
    private final IncidentWriteService incidentWriteService;
    private final TaskWriteService taskWriteService;

    @Autowired
    public IncidentController(
            final EntityEventFactory entityEventFactory,
            final IncidentService incidentService,
            final IncidentWriteService incidentWriteService,
            final TaskWriteService taskWriteService) {
        this.entityEventHandler = entityEventFactory.getEntityEventHandler(Incident.class);
        this.incidentService = incidentService;
        this.incidentWriteService = incidentWriteService;
        this.taskWriteService = taskWriteService;
    }

    @JsonView(JsonViews.Main.class)
    @Transactional
    @RequestMapping(value = "main", produces = "application/json", method = RequestMethod.GET)
    public SequencedResponse<List<Incident>> getForMain(@ActiveConcern Concern concern) {
        return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()),
                Initializer.init(incidentService.getAllRelevant(concern), Incident::getUnits, Incident::getPatient));
    }

    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    public RestResponse update(@RequestBody Incident incident, BindingResult result,
                               @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
        if (result.hasErrors()) {
            return new RestResponse(result);
        }

        boolean isNew = incident.getId() == null;
        incident = incidentWriteService.update(incident, concern, user);
        return new RestResponse(true, new RestProperty("new", isNew), new RestProperty("incident_id", incident.getId()));
    }

    @RequestMapping(value = "setToState", produces = "application/json", method = RequestMethod.POST)
    public RestResponse setToState(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id,
                                   @RequestParam("state") TaskState state, @AuthenticationPrincipal User user) {
        taskWriteService.changeState(incident_id, unit_id, state, user);
        return new RestResponse(true);
    }

    @RequestMapping(value = "assignPatient", produces = "application/json", method = RequestMethod.POST)
    public RestResponse assignPatient(@RequestParam("incident_id") int incident_id, @RequestParam("patient_id") int patient_id,
                                      @AuthenticationPrincipal User user) {
        incidentWriteService.assignPatient(incident_id, patient_id, user);
        return new RestResponse(true);
    }

}
