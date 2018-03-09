package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.ActiveConcern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("@auth.hasAccessLevel('Main')")
@RestController
@RequestMapping("/data/log")
public class LogController {

  @Autowired
  private LogService logService;

  @RequestMapping(value = "getCustom", produces = "application/json", method = RequestMethod.GET)
  public List<LogEntry> getCustom(@ActiveConcern Concern concern) {
    return logService.getCustom(concern);
  }

  @RequestMapping(value = "getLast/{count}", produces = "application/json", method = RequestMethod.GET)
  public List<LogEntry> getLast(@PathVariable("count") int count, @ActiveConcern Concern concern) {
    return logService.getLast(concern, count);
  }

  @RequestMapping(value = "getByUnit/{id}", produces = "application/json", method = RequestMethod.GET)
  public List<LogEntry> getByUnit(@PathVariable("id") int unit) {
    return logService.getByUnit(new Unit(unit));
  }

  @RequestMapping(value = "getLastByUnit/{id}/{limit}", produces = "application/json", method = RequestMethod.GET)
  public List<LogEntry> getLastByUnit(@PathVariable("id") int unit, @PathVariable("limit") int limit) {
    return logService.getLimitedByUnit(new Unit(unit), limit);
  }

  @RequestMapping(value = "getByIncident/{id}", produces = "application/json", method = RequestMethod.GET)
  public List<LogEntry> getByIncident(@PathVariable("id") int inc) {
    return logService.getByIncident(new Incident(inc));
  }

  @RequestMapping(value = "add", produces = "application/json", method = RequestMethod.POST)
  public RestResponse addEntry(@RequestBody LogEntry logEntry, BindingResult result,
          @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    if (result.hasErrors()) {
      return new RestResponse(result);
    }

    logService.logCustom(user, logEntry.getText(), concern, logEntry.getUnit(), logEntry.getIncident());
    return new RestResponse(true);
  }
}
