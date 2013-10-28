package at.wrk.coceso.controller;

import at.wrk.coceso.dao.CaseDao;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class WelcomeController {

    @Autowired
    CaseDao caseDao;

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

    @RequestMapping("/main")
    public String showMain(ModelMap model, HttpServletResponse response) {
        // TODO Read User and Set active Case

        return "main";
    }

    @RequestMapping("/main/{id}")
    public String showMainWithID(@PathVariable("id") int id, ModelMap model, HttpServletResponse response) {
        response.addCookie(new Cookie("active_case", id+""));

        return "main";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String showWelcome(ModelMap map) {

        List<Case> case_list = caseDao.getAll();
        map.addAttribute("case_list", case_list);

        Logger.debug("CaseList: size="+case_list.size());

        return "welcome";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.POST)
    public String welcomeRedirect(HttpServletRequest request) {
        int case_id = Integer.parseInt(request.getParameter("case_id"));

        if(request.getParameter("start") != null)
            return "redirect:/main/"+case_id;
        if(request.getParameter("edit") != null)
            return "redirect:/edit/"+case_id;

        return "redirect:/welcome";
    }

    @RequestMapping("/dashboard")
    public String showDashboard() {
        // not used in v1.0
        return "dashboard";
    }

}
