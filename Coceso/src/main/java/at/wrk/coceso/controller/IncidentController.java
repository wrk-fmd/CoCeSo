
package at.wrk.coceso.controller;

import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public List<Incident> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return incidentService.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("IncidentController: getAll: " + e);
            return null;
        }
    }

    @RequestMapping(value = "getAllActive", produces = "application/json")
    @ResponseBody
    public List<Incident> getAllActive(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return incidentService.getAllActive(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("IncidentController: getAll: " + e);
            return null;
        }
    }

    @RequestMapping(value = "getAllByState/{state}", produces = "application/json")
    @ResponseBody
    public List<Incident> getAllByState(@CookieValue(value = "active_case", defaultValue = "0") String case_id,
                                        @PathVariable("state") String s_state) {

        try {
            return incidentService.getAllByState(Integer.parseInt(case_id), IncidentState.valueOf(s_state));
        } catch(NumberFormatException e) {
            Logger.warning("IncidentController: getAll: " + e);
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Incident getByPost(@RequestParam("id") int id) {

        return incidentService.getById(id);
    }

    @Override
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Incident getByGet(@PathVariable("id") int id) {
        return getByPost(id);
    }

    @Override
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody Incident incident, BindingResult result,
                         @CookieValue(value = "active_case", defaultValue = "0") String case_id,
                         Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int caseId = Integer.parseInt(case_id);

        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        if(incident.getId() > 0) {
            Incident i = incidentService.getById(incident.getId());
            if(i.getConcern() != caseId)
                return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
        }


        incident.setConcern(caseId);

        if(incident.getConcern() <= 0) {
            return "{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}";
        }

        if(incident.getId() < 1) {
            incident.setId(0);

            incident.setId(incidentService.add(incident));
            //log.logFull(user, "Incident created", caseId, null, incident, true);
            return "{\"success\": " + (incident.getId() != -1) + ", \"new\": true, \"incident_id\":"+incident.getId()+"}";
        }

        //log.logFull(user, "Incident updated", caseId, null, incident, true);
        boolean ret = incidentService.update(incident, user);

        if(ret && incident.getState() == IncidentState.Done)
            taskService.checkStates(incident.getId(), user);

        return "{\"success\": " + ret + ", \"new\": false}";
    }

    @RequestMapping(value = "nextState/{incident_id}/{unit_id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> nextState(@PathVariable("incident_id") int incident_id,
                              @PathVariable("unit_id") int unit_id,
                              Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Incident incident = incidentService.getById(incident_id);
        TaskState newState = incident.nextState(unit_id);

        // Don't set State to ZAO if no AO is present. Except for SingleUnit Incidents (TODO Home can be null)
        if(newState == TaskState.ZAO && !incident.getType().isSingleUnit() && incident.getAo() == null) {
            return new ResponseEntity<String>("{\"success\":false," +
                    " \"message\":\"No AO in Incident given\"}",
                    HttpStatus.BAD_REQUEST);
        }

        if(newState == null)
            return new ResponseEntity<String>("{\"success\":false," +
                    " \"message\":\"Next State not possible, no Next State defined\"}",
                    HttpStatus.BAD_REQUEST);

        taskService.changeState(incident_id, unit_id, newState, user);

        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "nextState", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> nextStateByPost(@RequestParam("incident_id") int incident_id,
                              @RequestParam("unit_id") int unit_id,
                              Principal principal)
    {
        return nextState(incident_id, unit_id, principal);
    }

    @RequestMapping(value = "setToState/{incident_id}/{unit_id}/{state}",
            produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> setToState(@PathVariable("incident_id") int incident_id,
                               @PathVariable("unit_id") int unit_id,
                               @PathVariable("state") String s_state,
                               Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        TaskState state;

        try {
            state = TaskState.valueOf(s_state);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }

        taskService.changeState(incident_id, unit_id, state, user);

        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "setToState", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> setToStateByPost(@RequestParam("incident_id") int incident_id,
                               @RequestParam("unit_id") int unit_id,
                               @RequestParam("state") String s_state,
                               Principal principal)
    {
        return setToState(incident_id, unit_id, s_state, principal);
    }
}
