package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

    @RequestMapping("/main")
    public String showMain() {
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
