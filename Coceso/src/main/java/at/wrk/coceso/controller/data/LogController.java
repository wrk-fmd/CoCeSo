package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.LogDao;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data/log")
public class LogController {

  @Autowired
  LogDao dao;

  @Autowired
  LogService logService;

  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getLog(@CookieValue("concern") int concern_id) {
    return dao.getAll(concern_id);
  }

  @RequestMapping(value = "getCustom", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getCustom(@CookieValue("concern") int concern_id) {
    return logService.getCustom(concern_id);
  }

  @RequestMapping(value = "getAll/{concern_id}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getLogWithoutCookie(@PathVariable("concern_id") int concern_id) {
    return dao.getAll(concern_id);
  }

  @RequestMapping(value = "getLast/{count}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getLast(@PathVariable("count") int count, @CookieValue("concern") int concern_id) {
    return logService.getLast(concern_id, count);
  }

  @RequestMapping(value = "getByUnit/{id}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getByUnit(@PathVariable("id") int unit) {
    return logService.getByUnitId(unit);
  }

  @RequestMapping(value = "getLastByUnit/{id}/{limit}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getLastByUnit(@PathVariable("id") int unit, @PathVariable("limit") int limit) {
    return dao.getLimitedByUnitId(unit, limit);
  }

  @RequestMapping(value = "getByIncident/{id}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<LogEntry> getByIncident(@PathVariable("id") int id) {
    return logService.getByIncidentId(id);
  }

  @RequestMapping(value = "add", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String addEntry(@RequestBody LogEntry logEntry, BindingResult bindingResult,
          @CookieValue("concern") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    if (bindingResult.hasErrors()) {
      return "{\"success\":false, \"error\":\"Binding Error\"}";
    }

    logService.logFull(user, LogEntryType.CUSTOM.customMessage(logEntry.getText()), concern_id, logEntry.getUnit(), logEntry.getIncident(), false);
    return "{\"success\":true}";
  }
}
