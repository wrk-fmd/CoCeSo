package at.wrk.coceso.controller;

import at.wrk.coceso.dao.CaseDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Unit;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CreateController {

    @Autowired
    CaseDao caseDao;

    @Autowired
    UnitDao unitDao;

    @RequestMapping("/create")
    public String create(int id) {

        return "redirect:/edit/"+id;
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, ModelMap map) {

        Case caze = caseDao.getById(id);
        List<Unit> unit_list = unitDao.getAll(id);

        map.addAttribute("caze", caze);
        map.addAttribute("unit_list", unit_list);

        return "edit";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@RequestParam("id") int id, @RequestParam("name") String name,
                         @RequestParam("organiser") String organiser,
                         @RequestParam("pax") int pax) {

        Case caze = new Case();
        caze.id = id;
        caze.name = name;
        caze.pax = pax;
        caze.organiser = organiser;

        caseDao.update(caze);

        return "redirect:/edit/"+id;
    }


    @RequestMapping(value = "/updateUnit", method = RequestMethod.POST)
    public String updateUnit(HttpServletRequest request,
                         @CookieValue("active_case") int case_id) {

        Unit unit = new Unit();
        unit.id = Integer.parseInt(request.getParameter("id"));
        unit.call = request.getParameter("call");
        unit.ani = request.getParameter("ani");
        unit.info = request.getParameter("info");
        unit.portable = request.getParameter("portable") != null;
        unit.withDoc = request.getParameter("withDoc") != null;
        unit.transportVehicle = request.getParameter("transportVehicle") != null;

        unitDao.updateFull(unit);

        return "redirect:/edit/"+case_id;
    }

    @RequestMapping(value = "/createUnit", method = RequestMethod.POST)
    public String createUnit(HttpServletRequest request,
                             @CookieValue("active_case") int case_id) {

        Unit unit = new Unit();
        unit.aCase = new Case();
        unit.aCase.id = case_id;

        unit.id = -1;
        unit.call = request.getParameter("call");
        unit.ani = request.getParameter("ani");
        unit.info = request.getParameter("info");
        unit.portable = request.getParameter("portable") != null;
        unit.withDoc = request.getParameter("withDoc") != null;
        unit.transportVehicle = request.getParameter("transportVehicle") != null;

        unitDao.add(unit);

        Logger.debug("createUnit: Unit: "+unit.id+", "+unit.call);

        return "redirect:/edit/"+case_id;
    }
}
