package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/data/incident")
public class IncidentController implements IEntityController<Incident> {

  @Autowired
  IncidentService incidentService;

  //@Autowired
  //LogService log;
  @Autowired
  TaskService taskService;

  @Override
  @RequestMapping(value = "getAll", produces = "application/json")
  @ResponseBody
  public List<Incident> getAll(@CookieValue(value = "active_case", defaultValue = "0") int concernId) {
    return incidentService.getAll(concernId);
  }

  @RequestMapping(value = "getAllActive", produces = "application/json")
  @ResponseBody
  public List<Incident> getAllActive(@CookieValue(value = "active_case", defaultValue = "0") int concernId) {
    return incidentService.getAllActive(concernId);
  }

  @RequestMapping(value = "getAllRelevant", produces = "application/json")
  @ResponseBody
  public List<Incident> getAllRelevant(@CookieValue(value = "active_case", defaultValue = "0") int concernId) {
    return incidentService.getAllRelevant(concernId);
  }

  @RequestMapping(value = "getAllByState/{state}", produces = "application/json")
  @ResponseBody
  public List<Incident> getAllByState(@CookieValue(value = "active_case", defaultValue = "0") int concernId,
          @PathVariable("state") IncidentState state) {
    return incidentService.getAllByState(concernId, state);
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
          @CookieValue(value = "active_case", defaultValue = "0") int concernId,
          Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    if (result.hasErrors()) {
      return "{\"success\": false, description: \"Binding Error\"}";
    }

    if (incident.getId() > 0) {
      Incident i = incidentService.getById(incident.getId());
      if (i.getConcern() != concernId) {
        return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
      }
    }

    incident.setConcern(concernId);

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

      //log.logFull(user, "Incident created", caseId, null, incident, true);
      return "{\"success\": " + (incident.getId() > 0) + ", \"new\": true, \"incident_id\":" + incident.getId() + ",\"associated\":" + associated + "}";
    }

    //log.logFull(user, "Incident updated", caseId, null, incident, true);
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
  public ResponseEntity<String> nextState(@RequestParam("incident_id") int incident_id,
          @RequestParam("unit_id") int unit_id,
          Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    Incident incident = incidentService.getById(incident_id);
    TaskState newState = incident.nextState(unit_id);

    // Don't set State to ZAO if no AO is present. Except for SingleUnit Incidents (TODO Home can be null, remove middle statement if not)
    if (newState == TaskState.ZAO && !incident.getType().isSingleUnit() && Point.isEmpty(incident.getAo())) {
      return new ResponseEntity<String>("{\"success\":false,"
              + " \"message\":\"No AO in Incident given\"}",
              HttpStatus.BAD_REQUEST);
    }

    if (newState == null) {
      return new ResponseEntity<String>("{\"success\":false,"
              + " \"message\":\"Next State not possible, no Next State defined\"}",
              HttpStatus.BAD_REQUEST);
    }

    taskService.changeState(incident_id, unit_id, newState, user);

    return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
  }

  @RequestMapping(value = "setToState", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> setToState(
          @RequestParam("incident_id") int incident_id,
          @RequestParam("unit_id") int unit_id,
          @RequestParam("state") TaskState state,
          Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    return new ResponseEntity<String>("{\"success\":" + taskService.changeState(incident_id, unit_id, state, user) + "}", HttpStatus.OK);
  }

}
