
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
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
@RequestMapping("/data/unit")
public class UnitController implements IEntityController<Unit> {

    @Autowired
    private UnitService unitService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskService taskService;

    //@Autowired
    //private LogService log;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Unit> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return unitService.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("UnitController: getAll: "+e);
            return null;
        }
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Unit getByPost(@RequestParam(value = "id", required = true) int id) {

        return unitService.getById(id);
    }

    @Override
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit getByGet(@PathVariable("id") int id) {

        return getByPost(id);
    }

    @Override
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody Unit unit, BindingResult result,
                         @CookieValue(value = "active_case", defaultValue = "0") String case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int caseId = Integer.parseInt(case_id);

        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        if(unit.getId() > 0) {
            Unit u = unitService.getById(unit.getId());
            if(u.getConcern() != caseId)
                return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
        }


        unit.setConcern(caseId);

        if(unit.getConcern() <= 0) {
            return "{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}";
        }

        if(unit.getId() < 1) {
            unit.setId(0);

            unit.setId(unitService.add(unit, user));

            //log.logFull(user, "Unit created", caseId, unit, null, true);
            return "{\"success\": " + (unit.getId() != -1) + ", \"new\": true, \"unit_id\":"+ unit.getId() +"}";
        }

        //log.logFull(user, "Unit updated", caseId, unit, null, true);
        return "{\"success\": " + unitService.update(unit, user) + ", \"new\": false}";
    }

    @RequestMapping(value = "sendHome", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sendHomeByPost(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                         @RequestParam("id") int unitId, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        if(unitService.sendHome(activeCase, unitId, user)) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "holdPosition", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> holdPosition(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                                                 @RequestParam("id") int unitId, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        if(unitService.holdPosition(activeCase, unitId, user)) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "standby", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> standby(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                                               @RequestParam("id") int unitId, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        if(unitService.standby(activeCase, unitId, user)) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }
}
