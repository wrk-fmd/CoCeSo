package at.wrk.coceso.controller;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class CreateController {

    @Autowired
    ConcernDao concernDao;

    //@Autowired
    //LogService logService;

    @Autowired
    UnitService unitService;

    @RequestMapping(value = "/edit/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request) {

        Concern caze = new Concern();
        caze.name = request.getParameter("name");
        caze.info = request.getParameter("info");

        concernDao.add(caze);

        return "redirect:/welcome";
    }

    @RequestMapping("/edit")
    public String edit(@CookieValue("active_case") int id, ModelMap map) {

        Concern caze = concernDao.getById(id);
        List<Unit> unit_list = unitService.getAll(id);

        Logger.debug("unit_list.size(): "+unit_list.size());

        map.addAttribute("caze", caze);
        map.addAttribute("unit_list", unit_list);

        return "edit";
    }

    @RequestMapping(value = "/edit/update", method = RequestMethod.POST)
    public String updateCase(@RequestParam("id") int id, @RequestParam("name") String name,
                         @RequestParam("info") String info,
                         @RequestParam("pax") int pax) {

        Concern caze = new Concern();
        caze.id = id;
        caze.name = name;
        caze.pax = pax;
        caze.info = info;

        concernDao.update(caze);

        return "redirect:/edit";
    }


    @RequestMapping(value = "/edit/updateUnit", method = RequestMethod.POST)
    public String updateUnit(HttpServletRequest request,
                         @CookieValue("active_case") int case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.concern = case_id;
        unit.id = Integer.parseInt(request.getParameter("id"));
        unit.call = request.getParameter("call");
        unit.ani = request.getParameter("ani");
        unit.info = request.getParameter("info");
        unit.portable = request.getParameter("portable") != null;
        unit.withDoc = request.getParameter("withDoc") != null;
        unit.transportVehicle = request.getParameter("transportVehicle") != null;

        if(request.getParameter("update") != null) {
            unitService.updateFull(unit, user);
        }
        else if(request.getParameter("remove") != null) {
            unitService.remove(unit, user);
        }
        else {
            Logger.error("CreateController:updateUnit wrong submit button");
        }

        return "redirect:/edit";
    }

    @RequestMapping(value = "/edit/createUnit", method = RequestMethod.POST)
    public String createUnit(HttpServletRequest request,
                             @CookieValue("active_case") int case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.concern = case_id;

        unit.id = -1;
        unit.call = request.getParameter("call");
        unit.ani = request.getParameter("ani");
        unit.info = request.getParameter("info");
        unit.portable = request.getParameter("portable") != null;
        unit.withDoc = request.getParameter("withDoc") != null;
        unit.transportVehicle = request.getParameter("transportVehicle") != null;

        unitService.add(unit, user);

        Logger.debug("createUnit: Unit: "+unit.id+", "+unit.call);

        return "redirect:/edit";
    }

    @RequestMapping(value = "/edit/createUnitBatch", method = RequestMethod.POST)
    public String createUnitBatch(HttpServletRequest request,
                             @CookieValue("active_case") int case_id,
                             Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.concern = case_id;

        unit.id = -1;
        unit.portable = request.getParameter("portable") != null;
        unit.withDoc = request.getParameter("withDoc") != null;
        unit.transportVehicle = request.getParameter("transportVehicle") != null;

        int from = Integer.parseInt(request.getParameter("from"));
        int to = Integer.parseInt(request.getParameter("to"));

        for(int i = from; i <= to; i++){
            unit.call = request.getParameter("call_pre")+i;
            unitService.add(unit, user);
        }

        return "redirect:/edit";
    }
}
