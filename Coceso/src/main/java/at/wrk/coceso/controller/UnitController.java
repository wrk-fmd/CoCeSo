package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/data/units/")
public class UnitController {

    @RequestMapping("getUnits")
    public String getUnits() {

        return "";
    }

    @RequestMapping(value = "get")
    public String getUnit() {

        return "";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update() {

        return "";
    }

    @RequestMapping(value = "sendHome", method = RequestMethod.POST)
    public String sendHome(int unitId) {

        return "";
    }

}
