package at.wrk.coceso.endpoint;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.ActiveConcern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("@auth.hasAccessLevel('Main')")
@RestController
@RequestMapping("/data/log")
public class LogEndpoint {

    private final LogService logService;

    @Autowired
    public LogEndpoint(final LogService logService) {
        this.logService = logService;
    }

    @RequestMapping(value = "getCustom", produces = "application/json", method = RequestMethod.GET)
    public List<LogEntry> getCustom(@ActiveConcern final Concern concern) {
        return logService.getCustom(concern);
    }

    @RequestMapping(value = "getLast/{count}", produces = "application/json", method = RequestMethod.GET)
    public List<LogEntry> getLast(@PathVariable("count") final int count, @ActiveConcern final Concern concern) {
        return logService.getLast(concern, count);
    }

    @RequestMapping(value = "getByUnit/{id}", produces = "application/json", method = RequestMethod.GET)
    public List<LogEntry> getByUnit(@PathVariable("id") final int unitId) {
        return logService.getByUnit(new Unit(unitId));
    }

    @RequestMapping(value = "getLastByUnit/{id}/{limit}", produces = "application/json", method = RequestMethod.GET)
    public List<LogEntry> getLastByUnit(@PathVariable("id") final int unitId, @PathVariable("limit") final int limit) {
        return logService.getLimitedByUnit(new Unit(unitId), limit);
    }

    @RequestMapping(value = "getByIncident/{id}", produces = "application/json", method = RequestMethod.GET)
    public List<LogEntry> getByIncident(@PathVariable("id") final int inc) {
        return logService.getByIncident(new Incident(inc));
    }

    @RequestMapping(value = "add", produces = "application/json", method = RequestMethod.POST)
    public RestResponse addEntry(
            final @RequestBody LogEntry logEntry,
            final BindingResult result,
            final @ActiveConcern Concern concern) {
        if (result.hasErrors()) {
            return new RestResponse(result);
        }

        logService.logCustom(logEntry.getText(), concern, logEntry.getUnit(), logEntry.getIncident());
        return new RestResponse(true);
    }
}
