package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.IncidentSocketService;
import at.wrk.coceso.service.TaskSocketService;
import at.wrk.coceso.utils.ActiveConcern;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("@auth.hasAccessLevel('Main')")
@RestController
@RequestMapping("/data/incident")
public class IncidentController {

  private final EntityEventHandler<Incident> entityEventHandler = EntityEventHandler.getInstance(Incident.class);

  @Autowired
  private IncidentService incidentService;

  @Autowired
  private IncidentSocketService incidentSocketService;

  @Autowired
  private TaskSocketService taskSocketService;

  @JsonView(JsonViews.Main.class)
  @RequestMapping(value = "main", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Incident>> getForMain(@ActiveConcern Concern concern) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), incidentService.getAllRelevant(concern));
  }

  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public RestResponse update(@RequestBody Incident incident, BindingResult result,
      @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    if (result.hasErrors()) {
      return new RestResponse(result);
    }

    boolean isNew = incident.getId() == null;
    incident = incidentSocketService.update(incident, concern, user);
    return new RestResponse(true, new RestProperty("new", isNew), new RestProperty("incident_id", incident.getId()));
  }

  @RequestMapping(value = "setToState", produces = "application/json", method = RequestMethod.POST)
  public RestResponse setToState(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id,
      @RequestParam("state") TaskState state, @AuthenticationPrincipal User user) {
    taskSocketService.changeState(incident_id, unit_id, state, user);
    return new RestResponse(true);
  }

  @RequestMapping(value = "assignPatient", produces = "application/json", method = RequestMethod.POST)
  public RestResponse assignPatient(@RequestParam("incident_id") int incident_id, @RequestParam("patient_id") int patient_id,
      @AuthenticationPrincipal User user) {
    incidentSocketService.assignPatient(incident_id, patient_id, user);
    return new RestResponse(true);
  }

}
