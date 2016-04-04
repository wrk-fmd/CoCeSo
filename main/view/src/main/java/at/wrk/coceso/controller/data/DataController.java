package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.ClientLog;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.TaskSocketService;
import at.wrk.coceso.utils.ActiveConcern;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data/")
public class DataController {

  @Autowired
  private TaskSocketService taskSocketService;

  @Autowired
  private ConcernService concernService;

  @Autowired
  private PointService pointService;

  @Autowired
  private UserService userService;

  @PreAuthorize("@auth.hasAccessLevel('Main')")
  @RequestMapping(value = "assignUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse assignUnit(@RequestParam("incident_id") int incident_id, @RequestParam("unit_id") int unit_id,
      @AuthenticationPrincipal User user) {
    taskSocketService.changeState(incident_id, unit_id, TaskState.Assigned, user);
    return new RestResponse(true);
  }

  @PreAuthorize("permitAll")
  @RequestMapping(value = "timestamp", produces = "application/json", method = RequestMethod.GET)
  public RestResponse timestamp() {
    return new RestResponse(true, new RestProperty("time", System.currentTimeMillis()));
  }

  @PreAuthorize("isAuthenticated()")
  @RequestMapping(value = "jslog", produces = "application/json", method = RequestMethod.POST)
  public RestResponse jslog(@RequestBody ClientLog jslog, BindingResult result, @AuthenticationPrincipal User user) {
    jslog.log(user);
    return new RestResponse(true);
  }

  @PreAuthorize("isAuthenticated()")
  @RequestMapping(value = "setActiveConcern", produces = "application/json", method = RequestMethod.POST)
  public RestResponse setActiveConcern(@RequestParam("concern_id") Integer concern_id, @AuthenticationPrincipal User user) {
    Concern concern;

    if (concern_id == null) {
      concern = null;
    } else {
      concern = concernService.getById(concern_id);
      if (concern == null || concern.isClosed()) {
        return new RestResponse(Errors.ConcernMissingOrClosed);
      }
    }

    return new RestResponse(userService.setActiveConcern(user, concern));
  }

  @PreAuthorize("@auth.hasAccessLevel('Main')")
  @RequestMapping(value = "poiAutocomplete", produces = "application/json", method = RequestMethod.GET)
  public Collection<String> poiAutocomplete(@RequestParam("q") String q, @ActiveConcern Concern concern) {
    return pointService.autocomplete(q, concern);
  }
}
