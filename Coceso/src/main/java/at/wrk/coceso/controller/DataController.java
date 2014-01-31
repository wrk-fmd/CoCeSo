package at.wrk.coceso.controller;

import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("/data/")
public class DataController {

    @Autowired
    TaskService taskService;

    @Autowired
    LogService log;

    @ResponseBody
    @RequestMapping(value="assignUnit/{incidentid}/{unitid}", method = RequestMethod.GET, produces = "application/json")
    public String assignUnit(@PathVariable("incidentid") int incident_id, @PathVariable("unitid") int unit_id,
                             Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        return "{\"success\": " + taskService.changeState(incident_id, unit_id, TaskState.Assigned, user) + "}";
    }

    @ResponseBody
    @RequestMapping(value="assignUnit", method = RequestMethod.POST, produces = "application/json")
    public String assignUnitByPost(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id,
                             Principal principal)
    {
        return assignUnit(incident_id, unit_id, principal);
    }

    @ResponseBody
    @RequestMapping(value="timestamp", method = RequestMethod.GET, produces = "application/json")
    public String timestamp()
    {
        return "{\"time\":\"" + System.currentTimeMillis() + "\"}";
    }
}
