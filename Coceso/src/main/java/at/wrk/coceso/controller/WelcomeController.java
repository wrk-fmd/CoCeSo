package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }

}
