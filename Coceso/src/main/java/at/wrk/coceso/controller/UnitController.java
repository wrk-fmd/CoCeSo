
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.*;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.utils.LogText;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UnitDao unitDao;

    @Autowired
    private IncidentDao incidentDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LogService log;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Unit> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return unitDao.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("UnitController: getAll: "+e);
            return null;
        }
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Unit getByPost(@RequestParam(value = "id", required = true) int id) {

        return unitDao.getById(id);
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
        Person user = (Person) token.getPrincipal();

        int caseId = Integer.parseInt(case_id);

        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        if(unit.id > 0) {
            Unit u = unitDao.getById(unit.id);
            if(u.aCase.id != caseId)
                return "{\"success\": false, \"info\":\"Active Case not valid\"}";
        }


        unit.aCase = new Case();
        unit.aCase.id = caseId;

        if(unit.aCase.id <= 0) {
            return "{\"success\": false, \"info\":\"No active Case. Cookies enabled?\"}";
        }

        if(unit.id < 1) {
            unit.id = 0;

            unit.id = unitDao.add(unit);

            log.logFull(user, "Unit created", caseId, unit, null, true);
            return "{\"success\": " + (unit.id != -1) + ", \"new\": true, \"unit_id\":"+unit.id+"}";
        }

        log.logFull(user, "Unit updated", caseId, unit, null, true);
        return "{\"success\": " + unitDao.update(unit) + ", \"new\": false}";
    }

    @RequestMapping(value = "sendHome/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit sendHome(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                         @PathVariable("id") int unitId, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Person user = (Person) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        List<Incident> list = taskDao.getAllByUnitIdWithType(unitId);

        // If active Task != Standby or HoldPosition is present, don't send home
        for(Incident i : list) {
            if(i.type != IncidentType.HoldPosition && i.type != IncidentType.Standby)
                return null;
        }

        Unit unit = unitDao.getById(unitId);

        // Detach from all HoldPosition and Standby Incidents
        for(Incident i : list) {
            i.state = IncidentState.Done;
            log.logWithIDs(user.id, LogText.SEND_HOME_AUTO_DETACH, activeCase, unitId, i.id, true);
            incidentDao.update(i);
            taskDao.remove(i.id, unitId);
        }

        Incident toHome = new Incident();
        toHome.state = IncidentState.Dispo;
        toHome.aCase = new Case();
        toHome.aCase.id = activeCase;
        toHome.ao = unit.home;
        toHome.bo = unit.position; // TODO useful?
        toHome.type = IncidentType.Relocation;
        toHome.caller = user.getUsername(); // TODO useful?

        toHome.id = incidentDao.add(toHome);
        log.logFull(user, LogText.SEND_HOME_ASSIGN, activeCase, unit, toHome, true);
        taskService.assignUnit(toHome.id, unitId, user);

        return unit;
    }

}
