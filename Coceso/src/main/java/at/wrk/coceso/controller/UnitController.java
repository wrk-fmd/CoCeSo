
package at.wrk.coceso.controller;

import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Person;
import at.wrk.coceso.entities.Unit;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/data/unit/")
public class UnitController implements IEntityController<Unit> {

    @Autowired
    private UnitDao dao;

    @Autowired
    private LogService log;

    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Unit> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return dao.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            Logger.warning("UnitController: getAll: "+e);
            return null;
        }
    }

    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Unit getByPost(@RequestParam(value = "id", required = true) int id) {

        return dao.getById(id);
    }

    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit getByGet(@PathVariable("id") int id) {

        return getByPost(id);
    }

    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody Unit unit, BindingResult result,
                         @CookieValue(value = "active_case", defaultValue = "0") String case_id, Principal user)
    {
        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        unit.aCase = new Case();
        unit.aCase.id = Integer.parseInt(case_id);

        if(unit.id < 1) {
            unit.id = 0;
            log.log((Person)user, "Unit created", Integer.parseInt(case_id), unit, null, true);
            return "{\"success\": " + dao.add(unit) + ", \"new\": true}";
        }

        log.log((Person)user, "Unit updated", Integer.parseInt(case_id), unit, null, true);
        return "{\"success\": " + dao.update(unit) + ", \"new\": false}";
    }

    @RequestMapping(value = "sendHome/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit sendHome(@PathVariable("id") int unitId) {

        return null;
    }

}
