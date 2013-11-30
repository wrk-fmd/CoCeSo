package at.wrk.coceso.controller;

import at.wrk.coceso.dao.*;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Person;
import at.wrk.coceso.entities.Unit;
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
import java.util.List;

@Controller
public class WelcomeController {

    @Autowired
    CaseDao caseDao;

    @Autowired
    LogDao logDao;

    @Autowired
    IncidentDao incidentDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    TaskDao taskDao;

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

    @RequestMapping("/main")
    public String showMain(ModelMap model, @CookieValue("active_case") int case_id) {
        model.addAttribute("case_id", case_id);

        return "main";
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
        Person user = (Person) token.getPrincipal();

        map.addAttribute("user", user);

        List<Case> case_list = caseDao.getAll();
        map.addAttribute("case_list", case_list);

        Logger.debug("CaseList: size="+case_list.size());

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
                                @CookieValue(value = "active_case", defaultValue = "0") String caze)
    {
        int actCase = Integer.parseInt(caze);

        if(uid != null)
            map.addAttribute("uid", uid);
        if(iid != null)
            map.addAttribute("iid", iid);
        map.addAttribute("sub", s_sub);

        if(s_sub.equals("Unit")) {
            map.addAttribute("unit", "active");

            if(uid != null && uid > 0) {
                Unit ret = unitDao.getById(uid);
                if(ret == null) {
                    map.addAttribute("u_unit_failed", "No Unit found");
                }
                else {
                    map.addAttribute("u_unit", ret);
                }
            }
            else if(uid != null) {
                // Start of subpage Unit
            }
            else {
                map.addAttribute("units", unitDao.getAll(actCase));
            }

        } else if(s_sub.equals("Incident")) {
            map.addAttribute("incident", "active");

            if(uid != null && uid == -1) {
                map.addAttribute("incidents", incidentDao.getAllActive(actCase));
            } else if(iid != null) {
                map.addAttribute("i_incident", incidentDao.getById(iid));
            } else {
                map.addAttribute("incidents", incidentDao.getAll(actCase));
            }


        } else if(s_sub.equals("Task")) {
            map.addAttribute("task", "active");
            if((uid == null && iid == null) || (uid != null && iid != null)) {
                map.addAttribute("error", "No ID specified");
            }
            else if(uid != null) {
                map.addAttribute("tasks", taskDao.getAllByUnitId(uid));
            }
            else {
                map.addAttribute("tasks", taskDao.getAllByIncidentId(iid));
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
