package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/data/incidents/")
public class IncidentController {

    @RequestMapping("get")
    public String get() {

        return "";
    }

}
