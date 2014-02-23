
package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.LogDao;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/data/log")
public class LogController {

    @Autowired
    LogDao dao;

    @Autowired
    LogService log;

    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getLog(@CookieValue(value = "active_case", defaultValue = "0") String caze) {
        int case_id;
        try {
            case_id = Integer.parseInt(caze);
        }catch (NumberFormatException e) {
            Logger.debug(e.getMessage());
            return new LinkedList<LogEntry>();
        }
        return dao.getAll(case_id);
    }

    @RequestMapping(value = "getCustom", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getCustom(@CookieValue(value = "active_case", defaultValue = "0") String caze) {
        int case_id;
        try {
            case_id = Integer.parseInt(caze);
        }catch (NumberFormatException e) {
            Logger.debug(e.getMessage());
            return new LinkedList<LogEntry>();
        }
        return dao.getCustom(case_id);
    }

    @RequestMapping(value = "getAll/{case_id}", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getLogWithoutCookie(@PathVariable("case_id") int caze) {

        return dao.getAll(caze);
    }

    @RequestMapping(value = "getLast/{count}", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getLast(@PathVariable("count") int count, @CookieValue(value = "active_case", defaultValue = "0") String caze) {
        int case_id;
        try {
            case_id = Integer.parseInt(caze);
        }catch (NumberFormatException e) {
            Logger.debug(e.getMessage());
            return null;
        }

        return log.getLast(case_id, count);
    }


    @RequestMapping(value = "getByUnit/{id}", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getByUnit(@PathVariable("id") int unit) {

        return dao.getByUnitId(unit);
    }

    @RequestMapping(value = "getByIncident/{id}", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getByIncident(@PathVariable("id") int id) {

        return dao.getByIncidentId(id);
    }

    @RequestMapping(value = "add", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Object addEntry(@RequestBody LogEntry logEntry, BindingResult bindingResult,
                           @CookieValue(value = "active_case", defaultValue = "0") String case_id, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "{\"success\":false, \"error\":\"Binding Error\"}";
        }

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();


        log.logFull(user, LogEntryType.CUSTOM.customMessage(logEntry.getText()), Integer.parseInt(case_id),
                logEntry.getUnit(), logEntry.getIncident(), false);

        return "{\"success\":true}";
    }
}

