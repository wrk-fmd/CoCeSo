
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
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

        if(unit.id > 0) {
            Unit u = unitService.getById(unit.id);
            if(u.concern != caseId)
                return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
        }


        unit.concern = caseId;

        if(unit.concern <= 0) {
            return "{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}";
        }

        if(unit.id < 1) {
            unit.id = 0;

            unit.id = unitService.add(unit, user);

            //log.logFull(user, "Unit created", caseId, unit, null, true);
            return "{\"success\": " + (unit.id != -1) + ", \"new\": true, \"unit_id\":"+unit.id+"}";
        }

        //log.logFull(user, "Unit updated", caseId, unit, null, true);
        return "{\"success\": " + unitService.update(unit, user) + ", \"new\": false}";
    }

    @RequestMapping(value = "sendHome/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit sendHome(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                         @PathVariable("id") int unitId, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        List<Incident> list = taskDao.getAllByUnitIdWithType(unitId);

        // If active Task != Standby or HoldPosition is present, don't send home
        for(Incident i : list) {
            if(i.type != IncidentType.HoldPosition && i.type != IncidentType.Standby)
                return null;
        }

        Unit unit = unitService.getById(unitId);

        // Detach from all HoldPosition and Standby Incidents
        for(Incident i : list) {
            i.state = IncidentState.Done;
            //log.logWithIDs(user.id, LogText.SEND_HOME_AUTO_DETACH, activeCase, unitId, i.id, true);
            incidentService.update(i, user);
            taskDao.remove(i.id, unitId);
        }

        Incident toHome = new Incident();
        toHome.state = IncidentState.Dispo;
        toHome.concern = activeCase;
        toHome.ao = unit.home;
        toHome.bo = unit.position; // TODO useful?
        toHome.type = IncidentType.Relocation;
        toHome.caller = user.getUsername(); // TODO useful?

        toHome.id = incidentService.add(toHome, user);
        //log.logFull(user, LogText.SEND_HOME_ASSIGN, activeCase, unit, toHome, true);
        taskService.assignUnit(toHome.id, unitId, user);

        return unit;
    }

}
