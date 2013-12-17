package at.wrk.coceso.controller;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.entity.Concern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/main")
public class MainController {
    @Autowired
    ConcernDao concernDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showMain(ModelMap model, @CookieValue("active_case") int concern_id) {
        Concern c = concernDao.getById(concern_id);
        if(c == null)
            return "redirect:/welcome";

        model.addAttribute("concern", c);

        return "main";
    }

    @RequestMapping(value = "unit.html", method = RequestMethod.GET)
    public String unit() {

        return "main_content/unit";
    }

    @RequestMapping(value = "unit_form.html", method = RequestMethod.GET)
    public String unitForm() {

        return "main_content/unit_form";
    }

    @RequestMapping(value = "incident.html", method = RequestMethod.GET)
    public String incident() {

        return "main_content/incident";
    }

    @RequestMapping(value = "incident_form.html", method = RequestMethod.GET)
    public String incidentForm() {

        return "main_content/incident_form";
    }

    @RequestMapping(value = "license.html", method = RequestMethod.GET)
    public String license() {

        return "main_content/license";
    }

    @RequestMapping(value = "log.html", method = RequestMethod.GET)
    public String log() {

        return "main_content/log";
    }
}
