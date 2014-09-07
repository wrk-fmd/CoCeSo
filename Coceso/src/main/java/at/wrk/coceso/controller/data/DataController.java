package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.ClientLog;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.OperatorService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/data/")
public class DataController {

  @Autowired
  TaskService taskService;

  @Autowired
  ConcernService concernService;

  @Autowired
  OperatorService operatorService;

  @ResponseBody
  @RequestMapping(value = "assignUnit", produces = "application/json", method = RequestMethod.POST)
  public String assignUnit(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id, UsernamePasswordAuthenticationToken token) {
    return "{\"success\": " + taskService.changeState(incident_id, unit_id, TaskState.Assigned, (Operator) token.getPrincipal()) + "}";
  }

  @ResponseBody
  @RequestMapping(value = "timestamp", produces = "application/json", method = RequestMethod.GET)
  public String timestamp() {
    return "{\"time\":\"" + System.currentTimeMillis() + "\"}";
  }

  @ResponseBody
  @RequestMapping(value = "jslog", produces = "application/json", method = RequestMethod.POST)
  public boolean jslog(@RequestBody ClientLog jslog, BindingResult result, UsernamePasswordAuthenticationToken token) {
    jslog.log((Operator) token.getPrincipal());
    return true;
  }

  @ResponseBody
  @RequestMapping(value = "setActiveConcern", produces = "application/json", method = RequestMethod.POST)
  public String setActiveConcern(@RequestParam("concern_id") Integer concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    Concern concern;

    if (concern_id == null) {
      concern = null;
    } else {
      concern = concernService.getById(concern_id);
      if (concern == null || concern.isClosed()) {
        return "{\"success\":false,\"error\":1}";
      }
    }

    user.setActiveConcern(concern);
    return "{\"success\":" + operatorService.update(user) + "}";
  }

}
