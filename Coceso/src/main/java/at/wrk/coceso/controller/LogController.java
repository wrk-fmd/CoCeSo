
package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/data/log/")
public class LogController {

    @RequestMapping("get")
    public @ResponseBody String getLog() { //TODO optional Parameters Unit, Incident

        return "";
    }

    @RequestMapping("add")
    public @ResponseBody String addEntry() {

        return "";
    }
}

