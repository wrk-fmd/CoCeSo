package at.wrk.coceso.controller;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.entities.incidents.Incident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/data/incidents/")
public class IncidentController implements IEntityController<Incident> {

    @Autowired
    IncidentDao dao;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    public List<Incident> getAll(int caseId) {
        return null;
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    public Incident getByPost(@RequestParam("id") int id) {
        return null;
    }

    @Override
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    public Incident getByGet(@PathVariable("id") int id) {
        return getByPost(id);
    }

    @Override
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    public String update(Incident incident, BindingResult result) {
        return null;
    }
}
