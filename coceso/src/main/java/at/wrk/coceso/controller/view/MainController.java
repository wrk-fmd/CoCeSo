package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/main")
public class MainController {
    @Autowired
    ConcernService concernService;

    @Autowired
    UnitService unitService;

    @Autowired
    IncidentService incidentService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showMain(ModelMap model, @CookieValue("active_case") String c_id) {
        String error_return = "redirect:/welcome?error=1";

        if(c_id == null || c_id.isEmpty())
            return error_return;

        int concern_id;
        try {
            concern_id = Integer.parseInt(c_id);
        } catch (NumberFormatException nfe) {
            return error_return;
        }
        Concern c = concernService.getById(concern_id);
        if(c == null || c.isClosed())
            return error_return;

        model.addAttribute("concern", c);

        return "main";
    }

    @RequestMapping(value = "unit.html", method = RequestMethod.GET)
    public String unit() {

        return "main_content/unit";
    }

    @RequestMapping(value = "unit_hierarchy.html", method = RequestMethod.GET)
    public String unitHierarchy() {

        return "main_content/unit_hierarchy";
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

    /*@RequestMapping(value = "license.html", method = RequestMethod.GET)
    public String license() {

        return "main_content/license";
    }*/

    @RequestMapping(value = "log.html", method = RequestMethod.GET)
    public String log() {

        return "main_content/log";
    }

    @RequestMapping(value = "debug.html", method = RequestMethod.GET)
    public String debug() {

        return "main_content/debug";
    }

    @RequestMapping(value = "key.html", method = RequestMethod.GET)
    public String key() {

        return "main_content/key";
    }

    @RequestMapping(value = "patient_form.html", method = RequestMethod.GET)
    public String patientForm() {

        return "main_content/patient_form";
    }

    @RequestMapping(value = "log_add.html", method = RequestMethod.GET)
    public String logAdd() {

        return "main_content/log_add";
    }

    @RequestMapping(value = "dump.html", method = RequestMethod.GET)
    public String dump(ModelMap map, @CookieValue("active_case") int concern_id) {
        Concern c = concernService.getById(concern_id);
        if(c == null || c.isClosed())
            return "redirect:/welcome?error=1";

        map.addAttribute("concern", c);
        map.addAttribute("units", unitService.getAll(concern_id));
        map.addAttribute("incidents", incidentService.getAllActive(concern_id));
        map.addAttribute("date", new Date());

        return "main_content/dump";
    }
}
