package at.wrk.coceso.controller;

import at.wrk.coceso.dao.*;
import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WelcomeController {

    @Autowired
    ConcernDao concernDao;

    // Read Only use, TODO change to Service
    @Autowired
    LogDao logDao;

    @Autowired
    OperatorDao operatorDao;

    @Autowired
    IncidentService incidentService;

    @Autowired
    UnitService unitService;

    @Autowired
    TaskService taskService;

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(ModelMap model) {

        return "login";
    }

    @RequestMapping("/loginfailed")
    public String loginFailed(ModelMap model) {
        model.addAttribute("error", "true");
        return "login";
    }
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String showWelcome(ModelMap map, Principal principal) {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        user = operatorDao.getById(user.getId());

        map.addAttribute("user", user);

        List<Concern> concern_list = concernDao.getAll();
        map.addAttribute("concern_list", concern_list);

        Logger.debug("CaseList: size="+ concern_list.size());

        return "welcome";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.POST)
    public String welcomeRedirect(HttpServletRequest request, HttpServletResponse response) {
        int case_id;
        try {
            case_id = Integer.parseInt(request.getParameter("case_id"));
        } catch (NumberFormatException e) {
            return "redirect:/welcome";
        }

        response.addCookie(new Cookie("active_case", case_id+""));

        if(request.getParameter("start") != null)
            return "redirect:/main";
        if(request.getParameter("edit") != null)
            return "redirect:/edit";

        return "redirect:/welcome";
    }


    @RequestMapping("/dashboard")
    public String showDashboard(@RequestParam(value = "sub", defaultValue = "Log") String s_sub,
                                @RequestParam(value = "uid", required = false) Integer uid,
                                @RequestParam(value = "iid", required = false) Integer iid,
                                ModelMap map,
                                @RequestParam(value = "concern", defaultValue = "0") String caze)
    {
        int actCase = Integer.parseInt(caze);
        map.addAttribute("concern", actCase);

        map.addAttribute("concerns", concernDao.getAll());

        if(uid != null)
            map.addAttribute("uid", uid);
        if(iid != null)
            map.addAttribute("iid", iid);
        map.addAttribute("sub", s_sub);

        if(s_sub.equals("Unit")) {
            map.addAttribute("unit", "active");
            map.addAttribute("sel_units", unitService.getAll(actCase));

            if(uid != null && uid > 0) {
                Unit ret = unitService.getById(uid);
                if(ret == null) {
                    map.addAttribute("error", "No Unit found");
                }
                else {
                    map.addAttribute("u_unit", ret);

                }
            }
            else {
                map.addAttribute("units", unitService.getAll(actCase));
            }

        } else if(s_sub.equals("Incident")) {
            map.addAttribute("incident", "active");

            if(uid != null && uid == -1) {
                map.addAttribute("incidents", incidentService.getAllActive(actCase));
            } else if(iid != null) {
                Incident ret = incidentService.getById(iid);

                if(ret == null) {
                    map.addAttribute("error", "No Incident Found");
                }
                else {
                    Map<Integer, String> i_map = new HashMap<Integer, String>();
                    for(Map.Entry<Integer, TaskState> entry : ret.getUnits().entrySet()) {
                        Unit u = unitService.getById(entry.getKey());
                        i_map.put(u.getId(), u.getCall());
                    }
                    map.addAttribute("i_map", i_map);

                    map.addAttribute("i_incident", ret);
                }
            } else {
                map.addAttribute("incidents", incidentService.getAll(actCase));
            }


        } else if(s_sub.equals("Task")) {
            map.addAttribute("task", "active");
            if((uid == null && iid == null) || (uid != null && iid != null)) {
                map.addAttribute("error", "No ID specified");
            }
            else if(uid != null) {
                map.addAttribute("tasks", taskService.getAllByUnitId(uid));
            }
            else {
                map.addAttribute("tasks", taskService.getAllByIncidentId(iid));
            }

        } else {
            map.addAttribute("log", "active");
            if((uid == null && iid == null) || (uid != null && iid != null)) {
                map.addAttribute("logs", logDao.getAll(actCase));
            }
            else if(uid != null) {
                map.addAttribute("logs", logDao.getByUnitId(uid));
            }
            else {
                map.addAttribute("logs", logDao.getByIncidentId(iid));
            }
        }


        return "dashboard";
    }

}
