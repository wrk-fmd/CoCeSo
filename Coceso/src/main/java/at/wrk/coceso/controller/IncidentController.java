
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Incident;
import at.wrk.coceso.entities.Person;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/data/incidents")
public class IncidentController implements IEntityController<Incident> {

    @Autowired
    IncidentDao dao;

    @Autowired
    LogService log;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Incident> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return dao.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("IncidentController: getAll: " + e);
            return null;
        }
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Incident getByPost(@RequestParam("id") int id) {

        return dao.getById(id);
    }

    @Override
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Incident getByGet(@PathVariable("id") int id) {
        return getByPost(id);
    }

    @Override
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(Incident incident, BindingResult result,
                         @CookieValue(value = "active_case", defaultValue = "0") String case_id,
                         Principal user)
    {
        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        incident.aCase = new Case();
        incident.aCase.id = Integer.parseInt(case_id);

        if(incident.id < 1) {
            incident.id = 0;
            log.log((Person)user, "Incident created", Integer.parseInt(case_id), null, incident, true);
            return "{\"success\": " + dao.add(incident) + ", \"new\": true}";
        }

        log.log((Person)user, "Incident updated", Integer.parseInt(case_id), null, incident, true);
        return "{\"success\": " + dao.update(incident) + ", \"new\": false}";
    }

}
