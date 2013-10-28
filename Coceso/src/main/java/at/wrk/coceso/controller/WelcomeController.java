package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class WelcomeController {

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

    @RequestMapping("/main")
    public String showMain(ModelMap model, HttpServletResponse response) {
        // TODO Read User and Set active Case
        response.addCookie(new Cookie("active_case", "1"));

        return "main";
    }

    @RequestMapping("/welcome")
    public String showWelcome() {
        return "welcome";
    }

    @RequestMapping("/dashboard")
    public String showDashboard() {
        // not used in v1.0
        return "dashboard";
    }

    @RequestMapping("/create")
    public String showCreate() {

        return "create";
    }


}
