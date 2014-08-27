package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.OperatorService;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/data/")
public class DataController {

  private static final Logger logger = Logger.getLogger("CoCeSo");

  // TODO Authority to Close and Reopen Concerns
  private static final CocesoAuthority CLOSE_AUTHORITY = CocesoAuthority.Root;

  @Autowired
  TaskService taskService;

  @Autowired
  ConcernService concernService;

  @Autowired
  OperatorService operatorService;

  @ResponseBody
  @RequestMapping(value = "assignUnit", produces = "application/json", method = RequestMethod.POST)
  public String assignUnit(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    return "{\"success\": " + taskService.changeState(incident_id, unit_id, TaskState.Assigned, user) + "}";
  }

  @ResponseBody
  @RequestMapping(value = "timestamp", produces = "application/json", method = RequestMethod.GET)
  public String timestamp() {
    return "{\"time\":\"" + System.currentTimeMillis() + "\"}";
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

  @ResponseBody
  @RequestMapping(value = "closeConcern", produces = "application/json", method = RequestMethod.POST)
  public String closeConcern(@RequestParam("concern_id") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    Concern concern = concernService.getById(concern_id);
    if (concern == null || concern.isClosed()) {
      return "{\"success\":false,\"error\":" + (concern == null ? 2 : 3) + "}";
    }

    if (!user.getInternalAuthorities().contains(CLOSE_AUTHORITY)) {
      logger.log(Level.WARNING, "User {0} tried to close Concern \"{1}\" without Authority \"{2}\"", new Object[]{user.getUsername(), concern.getName(), CLOSE_AUTHORITY});
      return "{\"success\":false,\"error\":5}";
    }

    logger.log(Level.INFO, "/data/closeConcern[POST]: user {0} closed Concern #{1}", new Object[]{user.getUsername(), concern.getId()});
    concern.setClosed(true);
    return "{\"success\":" + concernService.update(concern, user) + "}";
  }

  @ResponseBody
  @RequestMapping(value = "reopenConcern", produces = "application/json", method = RequestMethod.POST)
  public String reopenConcern(@RequestParam("concern_id") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    Concern concern = concernService.getById(concern_id);
    if (concern == null || !concern.isClosed()) {
      return "{\"success\":false,\"error\":" + (concern == null ? 2 : 4) + "}";
    }

    if (!user.getInternalAuthorities().contains(CLOSE_AUTHORITY)) {
      logger.log(Level.WARNING, "User {0} tried to reopen Concern \"{1}\" without Authority \"{2}\"", new Object[]{user.getUsername(), concern.getName(), CLOSE_AUTHORITY});
      return "{\"success\":false,\"error\":5}";
    }

    logger.log(Level.INFO, "/data/reopenConcern[POST]: user {0} reopened Concern #{1}", new Object[]{user.getUsername(), concern.getId()});
    concern.setClosed(false);
    return "{\"success\":" + concernService.update(concern, user) + "}";
  }

}
