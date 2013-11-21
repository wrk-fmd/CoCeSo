package at.wrk.coceso.controller;

import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/data/")
public class DataController {

    @Autowired
    TaskService taskService;

    @RequestMapping(value="assignUnit/{incidentid}/{unitid}", method = RequestMethod.GET)
    public String assignUnit(@PathVariable("incidentid") int incident_id, @PathVariable("unitid") int unit_id) {

        return "{\"success\": " + taskService.assignUnit(incident_id, unit_id) + "}";
    }
}
