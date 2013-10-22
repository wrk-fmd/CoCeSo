package at.wrk.coceso.controller;

import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data/units/")
public class UnitController implements IEntityController<Unit> {

    @Autowired
    private UnitDao dao;

    @RequestMapping(value = "getAll", produces = "application/json")
    public List<Unit> getAll(@CookieValue("active_case") int case_id) {

        return dao.getAll(case_id);
    }

    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    public Unit getByPost(@RequestParam(value = "id", required = true) int id) {

        return dao.getById(id);
    }

    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    public Unit getByGet(@PathVariable("id") int id) {

        return getByPost(id);
    }

    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    public String update(Unit unit, BindingResult result) {
        if(result.hasErrors()) {
            return "{success: false}";
        }
        if(unit.id < 1) {
            return "{success: " + dao.add(unit) + "}";
        }

        return "{success: " + dao.update(unit) + "}";
    }

    @RequestMapping(value = "sendHome", produces = "application/json", method = RequestMethod.POST)
    public String sendHome(int unitId) {

        return "{success: " + dao.sendHome(unitId) + "}";
    }

}
