package at.wrk.coceso.controller;

import at.wrk.coceso.dao.*;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Person;
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
    public String showDashboard(@RequestParam(value = "sub", defaultValue = "Log") String s_sub, ModelMap map,
                                @CookieValue(value = "active_case", defaultValue = "0") String caze)
    {
        int actCase = Integer.parseInt(caze);

        if(s_sub.equals("Unit")) {
            map.addAttribute("unit", "active");
            map.addAttribute("units", unitDao.getAll(actCase));

        } else if(s_sub.equals("Incident")) {
            map.addAttribute("incident", "active");
            map.addAttribute("incidents", incidentDao.getAll(actCase));

        } else {
            map.addAttribute("log", "active");
            map.addAttribute("logs", logDao.getAll(actCase));
        }


        return "dashboard";
    }

}
