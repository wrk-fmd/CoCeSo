package at.wrk.coceso.controller;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Point;
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
@RequestMapping("/edit")
public class CreateController {

    @Autowired
    ConcernDao concernDao;

    //@Autowired
    //LogService logService;

    @Autowired
    UnitService unitService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(HttpServletRequest request) {

        Concern caze = new Concern();
        caze.setName(request.getParameter("name"));
        caze.setInfo(request.getParameter("info"));

        concernDao.add(caze);

        return "redirect:/welcome";
    }

    @RequestMapping("")
    public String edit(@CookieValue(value = "active_case", defaultValue = "0") String c_id, ModelMap map) {

        int id = Integer.parseInt(c_id);

        if(id == 0) {
            //TODO Show Error Message
            return "redirect:/welcome";
        }

        Concern caze = concernDao.getById(id);
        List<Unit> unit_list = unitService.getAll(id);

        Logger.debug("unit_list.size(): "+unit_list.size());

        map.addAttribute("caze", caze);
        map.addAttribute("unit_list", unit_list);

        return "edit";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String updateCase(@RequestParam("id") int id, @RequestParam("name") String name,
                         @RequestParam("info") String info,
                         @RequestParam("pax") int pax) {

        Concern caze = new Concern();
        caze.setId(id);
        caze.setName(name);
        caze.setPax(pax);
        caze.setInfo(info);

        concernDao.update(caze);

        return "redirect:/edit";
    }


    @RequestMapping(value = "updateUnit", method = RequestMethod.POST)
    public String updateUnit(HttpServletRequest request,
                         @CookieValue("active_case") int case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();



        Unit unit = unitService.getById(Integer.parseInt(request.getParameter("id")));

        unit.setCall(request.getParameter("call"));
        unit.setAni(request.getParameter("ani"));
        unit.setInfo(request.getParameter("info"));
        unit.setPortable(request.getParameter("portable") != null);
        unit.setWithDoc(request.getParameter("withDoc") != null);
        unit.setTransportVehicle(request.getParameter("transportVehicle") != null);

        String home = request.getParameter("home");
        if(home != null)
            unit.setHome(new Point(home));


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

    @RequestMapping(value = "createUnit", method = RequestMethod.POST)
    public String createUnit(HttpServletRequest request,
                             @CookieValue("active_case") int case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.setConcern(case_id);

        unit.setId(-1);
        unit.setCall(request.getParameter("call"));
        unit.setAni(request.getParameter("ani"));
        unit.setInfo(request.getParameter("info"));
        unit.setPortable(request.getParameter("portable") != null);
        unit.setWithDoc(request.getParameter("withDoc") != null);
        unit.setTransportVehicle(request.getParameter("transportVehicle") != null);

        String home = request.getParameter("home");
        if(home != null)
            unit.setHome(new Point(home));

        unitService.add(unit, user);

        Logger.debug("createUnit: Unit: "+ unit.getId() +", "+ unit.getCall());

        return "redirect:/edit";
    }

    @RequestMapping(value = "createUnitBatch", method = RequestMethod.POST)
    public String createUnitBatch(HttpServletRequest request,
                             @CookieValue("active_case") int case_id,
                             Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.setConcern(case_id);

        unit.setId(-1);
        unit.setPortable(request.getParameter("portable") != null);
        unit.setWithDoc(request.getParameter("withDoc") != null);
        unit.setTransportVehicle(request.getParameter("transportVehicle") != null);
        unit.setHome(new Point(request.getParameter("home")));

        int from = Integer.parseInt(request.getParameter("from"));
        int to = Integer.parseInt(request.getParameter("to"));

        for(int i = from; i <= to; i++){
            unit.setCall(request.getParameter("call_pre")+i);
            unitService.add(unit, user);
        }

        return "redirect:/edit";
    }
}
