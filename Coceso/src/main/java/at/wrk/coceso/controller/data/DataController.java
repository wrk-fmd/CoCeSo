package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/data/")
public class DataController {

  @Autowired
  TaskService taskService;

  @Autowired
  LogService log;

  @ResponseBody
  @RequestMapping(value = "assignUnit", produces = "application/json", method = RequestMethod.POST)
  public String assignUnit(
          @RequestParam("incident_id") int incident_id,
          @RequestParam("unit_id") int unit_id,
          Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    return "{\"success\": " + taskService.changeState(incident_id, unit_id, TaskState.Assigned, user) + "}";
  }

  @ResponseBody
  @RequestMapping(value = "timestamp", produces = "application/json", method = RequestMethod.GET)
  public String timestamp() {
    return "{\"time\":\"" + System.currentTimeMillis() + "\"}";
  }
}
