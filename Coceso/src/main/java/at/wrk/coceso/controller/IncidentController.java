
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.entities.Incident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data/incidents")
public class IncidentController implements IEntityController<Incident> {

    @Autowired
    IncidentDao dao;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Incident> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {
        return null;
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Incident getByPost(@RequestParam("id") int id) {
        return null;
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
    public String update(Incident incident, BindingResult result,@CookieValue(value = "active_case", defaultValue = "0") String case_id) {
        return null;
    }

}
