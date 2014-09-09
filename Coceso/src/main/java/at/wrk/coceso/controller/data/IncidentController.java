package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/data/incident")
public class IncidentController implements IEntityController<Incident> {

  @Autowired
  private IncidentService incidentService;

  @Autowired
  private TaskService taskService;

  @Override
  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<Incident> getAll(@CookieValue("concern") int concern_id) {
    return incidentService.getAll(concern_id);
  }

  @RequestMapping(value = "getAllActive", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<Incident> getAllActive(@CookieValue("concern") int concern_id) {
    return incidentService.getAllActive(concern_id);
  }

  @RequestMapping(value = "getAllRelevant", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<Incident> getAllRelevant(@CookieValue("concern") int concern_id) {
    return incidentService.getAllRelevant(concern_id);
  }

  @RequestMapping(value = "getAllByState/{state}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<Incident> getAllByState(@CookieValue("concern") int concern_id, @PathVariable("state") IncidentState state) {
    return incidentService.getAllByState(concern_id, state);
  }

  @Override
  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public Incident getById(@PathVariable("id") int id) {
    return incidentService.getById(id);
  }

  @Override
  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String update(@RequestBody Incident incident, BindingResult result,
          @CookieValue("concern") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    if (result.hasErrors()) {
      return "{\"success\": false, description: \"Binding Error\"}";
    }

    if (incident.getId() > 0) {
      Incident i = incidentService.getById(incident.getId());
      if (i.getConcern() != concern_id) {
        return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
      }
    }

    incident.setConcern(concern_id);

    if (incident.getConcern() <= 0) {
      return "{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}";
    }

    if (incident.getId() < 1) {
      incident.setId(0);

      incident.setId(incidentService.add(incident, user));

      String associated = "{}";
      if (incident.getId() > 0) {
        associated = setAssociated(incident, user);
      }

      //log.logFull(user, "Incident created", concern_id, null, incident, true);
      return "{\"success\": " + (incident.getId() > 0) + ", \"new\": true, \"incident_id\":" + incident.getId() + ",\"associated\":" + associated + "}";
    }

    //log.logFull(user, "Incident updated", concern_id, null, incident, true);
    boolean ret = incidentService.update(incident, user);
    String associated = "{}";

    if (ret && incident.getState() == IncidentState.Done) {
      taskService.checkStates(incident.getId(), user);
    } else if (ret) {
      associated = setAssociated(incident, user);
    }

    return "{\"success\": " + ret + ", \"new\": false,\"associated\":" + associated + "}";
  }

  protected String setAssociated(Incident incident, Operator user) {
    String messages = "{";
    for (Map.Entry<Integer, TaskState> entry : incident.getUnits().entrySet()) {
      if (messages.length() > 1) {
        messages += ",";
      }
      messages += "\"" + entry.getKey() + "\":" + taskService.changeState(incident.getId(), entry.getKey(), entry.getValue(), user);
    }
    messages += "}";
    return messages;
  }

  @RequestMapping(value = "nextState", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String nextState(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id,
          UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    Incident incident = incidentService.getById(incident_id);
    TaskState newState = incident.nextState(unit_id);

    // Don't set State to ZAO if no AO is present. Except for SingleUnit Incidents (TODO Home can be null, remove middle statement if not)
    if (newState == TaskState.ZAO && !incident.getType().isSingleUnit() && Point.isEmpty(incident.getAo())) {
      return "{\"success\":false,\"message\":\"No AO in Incident given\"}";
    }

    if (newState == null) {
      return "{\"success\":false,\"message\":\"Next State not possible, no Next State defined\"}";
    }

    taskService.changeState(incident_id, unit_id, newState, user);
    return "{\"success\":true}";
  }

  @RequestMapping(value = "setToState", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String setToState(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id,
          @RequestParam("state") TaskState state, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    return "{\"success\":" + taskService.changeState(incident_id, unit_id, state, user) + "}";
  }

}
