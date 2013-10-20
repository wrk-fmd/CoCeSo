package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/data/log/")
public class LogController {

    @RequestMapping("get")
    public String getLog() { //TODO optional Parameters Unit, Incident

        return "";
    }

    @RequestMapping("add")
    public String addEntry() {

        return "";
    }
}
