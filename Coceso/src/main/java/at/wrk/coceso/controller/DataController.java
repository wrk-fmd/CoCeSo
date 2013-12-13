package at.wrk.coceso.controller;

import at.wrk.coceso.entities.Operator;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;


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

        return "{\"success\": " + taskService.assignUnit(incident_id, unit_id, user) + "}";
    }
}
