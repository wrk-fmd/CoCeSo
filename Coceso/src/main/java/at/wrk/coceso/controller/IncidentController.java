
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.entities.*;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    IncidentDao dao;

    @Autowired
    LogService log;

    @Autowired
    TaskService taskService;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Incident> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return dao.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("IncidentController: getAll: " + e);
            return null;
        }
    }

    @RequestMapping(value = "getAllActive", produces = "application/json")
    @ResponseBody
    public List<Incident> getAllActive(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return dao.getAllActive(Integer.parseInt(case_id));
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
            return dao.getAllByState(Integer.parseInt(case_id), IncidentState.valueOf(s_state));
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

        return dao.getById(id);
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
        Person user = (Person) token.getPrincipal();

        int caseId = Integer.parseInt(case_id);

        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        if(incident.id > 0) {
            Incident i = dao.getById(incident.id);
            if(i.aCase.id != caseId)
                return "{\"success\": false, \"info\":\"Active Case not valid\"}";
        }

        incident.aCase = new Case();
        incident.aCase.id = caseId;

        if(incident.aCase.id <= 0) {
            return "{\"success\": false, \"info\":\"No active Case. Cookies enabled?\"}";
        }

        if(incident.id < 1) {
            incident.id = 0;

            incident.id = dao.add(incident);
            log.logFull(user, "Incident created", caseId, null, incident, true);
            return "{\"success\": " + (incident.id != -1) + ", \"new\": true, \"incident_id\":"+incident.id+"}";
        }

        log.logFull(user, "Incident updated", caseId, null, incident, true);
        boolean ret = dao.update(incident);

        if(ret && incident.state == IncidentState.Done)
            taskService.checkStates(incident.id, user);

        return "{\"success\": " + ret + ", \"new\": false}";
    }

    @RequestMapping(value = "nextState/{incident_id}/{unit_id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Incident nextState(@PathVariable("incident_id") int incident_id,
                              @PathVariable("unit_id") int unit_id,
                              Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Person user = (Person) token.getPrincipal();

        Incident incident = dao.getById(incident_id);
        TaskState newState = incident.nextState(unit_id);

        if(newState == null)
            return null;

        taskService.changeState(incident_id, unit_id, newState, user);

        return dao.getById(incident_id);
    }

    @RequestMapping(value = "setToState/{incident_id}/{unit_id}/{state}",
            produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Incident setToState(@PathVariable("incident_id") int incident_id,
                               @PathVariable("unit_id") int unit_id,
                               @PathVariable("state") String s_state,
                               Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Person user = (Person) token.getPrincipal();

        TaskState state;

        try {
            state = TaskState.valueOf(s_state);
        } catch (IllegalArgumentException e) {
            return null;
        }

        taskService.changeState(incident_id, unit_id, state, user);

        return dao.getById(incident_id);
    }
}
