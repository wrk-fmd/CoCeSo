package at.wrk.coceso.controller.data;

import at.wrk.coceso.contract.client.ClientLog;
import at.wrk.coceso.controller.handler.ClientLogger;
import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.TaskWriteService;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.utils.ActiveConcern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequestMapping("/data/")
public class DataController {

    private final TaskWriteService taskWriteService;
    private final ConcernService concernService;
    private final PointService pointService;
    private final UserService userService;
    private final ClientLogger clientLogger;

    @Autowired
    public DataController(
            final TaskWriteService taskWriteService,
            final ConcernService concernService,
            final PointService pointService,
            final UserService userService,
            final ClientLogger clientLogger) {
        this.taskWriteService = taskWriteService;
        this.concernService = concernService;
        this.pointService = pointService;
        this.userService = userService;
        this.clientLogger = clientLogger;
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @RequestMapping(value = "assignUnit", produces = "application/json", method = RequestMethod.POST)
    public RestResponse assignUnit(
            @RequestParam("incident_id") final int incidentId,
            @RequestParam("unit_id") final int unitId) {
        taskWriteService.assignUnit(incidentId, unitId);
        return new RestResponse(true);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "timestamp", produces = "application/json", method = RequestMethod.GET)
    public RestResponse timestamp() {
        return new RestResponse(true, new RestProperty("time", System.currentTimeMillis()));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "clientLogger", produces = "application/json", method = RequestMethod.POST)
    public RestResponse clientLogger(
            @RequestBody final ClientLog clientLog,
            final HttpServletRequest request) {
        clientLogger.handleClientLog(clientLog, request.getRemoteHost());
        return new RestResponse(true);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "setActiveConcern", produces = "application/json", method = RequestMethod.POST)
    public RestResponse setActiveConcern(
            @RequestParam("concern_id") final Integer concernId,
            @AuthenticationPrincipal final AuthenticatedUser user) {
        Concern concern;

        if (concernId == null) {
            concern = null;
        } else {
            concern = concernService.getById(concernId);
            if (concern == null || concern.isClosed()) {
                return new RestResponse(Errors.ConcernMissingOrClosed);
            }
        }

        return new RestResponse(userService.setActiveConcern(user, concern));
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @RequestMapping(value = "poiAutocomplete", produces = "application/json", method = RequestMethod.GET)
    public Collection<String> poiAutocomplete(@RequestParam("q") final String searchQuery, @ActiveConcern final Concern concern) {
        return pointService.autocomplete(searchQuery, concern);
    }
}
