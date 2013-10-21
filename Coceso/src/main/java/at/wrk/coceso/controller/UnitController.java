package at.wrk.coceso.controller;

import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/data/units/")
public class UnitController {

    @Autowired
    private UnitDao dao;

    @RequestMapping(value = "getUnits", produces = "application/json")
    public List<Unit> getUnits(@CookieValue("active_case") int case_id) {

        return dao.getAll(case_id);
    }

    @RequestMapping(value = "get", produces = "application/json")
    public Unit getUnit(@RequestParam(value = "id", required = true) int id) {

        return dao.getById(id);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST, produces = "application/json")
    public String update(Unit unit, BindingResult result) {
        if(result.hasErrors()) {
            return "{success: false}";
        }
        if(unit.id < 1) {
            return "{success: " + dao.add(unit) + "}";
        }

        return "{success: " + dao.update(unit) + "}";
    }

    @RequestMapping(value = "sendHome", method = RequestMethod.POST, produces = "application/json")
    public String sendHome(int unitId) {

        return "{success: " + dao.sendHome(unitId) + "}";
    }

}
