package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/data/")
public class DataController {

    @RequestMapping("getUnits")
    public String getUnits() {

        return "";
    }

}
