
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.LogDao;
import at.wrk.coceso.entities.Incident;
import at.wrk.coceso.entities.LogEntry;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data/log/")
public class LogController {

    @Autowired
    LogDao dao;

    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getLog(@CookieValue(value = "active_case", defaultValue = "0") String caze) {
        int case_id = Integer.parseInt(caze);

        return dao.getAll(case_id);
    }

    @RequestMapping(value = "getAll/{case_id}", produces = "application/json")
    @ResponseBody
    public List<LogEntry> getLogWithoutCookie(@PathVariable("case_id") int caze) {

        return dao.getAll(caze);
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
    public Object addEntry(@RequestBody LogEntry log, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new Object() {
                boolean success = false;
                String error = "Binding Error";
            };
        }

        //TODO Read User ID
        return "{\"success\":"+dao.add(log)+"}";
    }
}

