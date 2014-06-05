package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Controller
@RequestMapping("/edit")
public class ConcernEditController {

    Logger logger = Logger.getLogger("CoCeSo");

    private static Set<Integer> allowedErrors;

    static {
        allowedErrors = new HashSet<Integer>();
        allowedErrors.add(3);
    }

    @Autowired
    //ConcernDao concernService;
            ConcernService concernService;

    //@Autowired
    //LogService logService;

    @Autowired
    UnitService unitService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Concern concern = new Concern();
        concern.setName(request.getParameter("name"));
        concern.setInfo(request.getParameter("info"));

        int ret = concernService.add(concern, user);

        return "redirect:/welcome" + (ret == -3 ? "?error=3" : "");
    }

    @RequestMapping("")
    public String edit(@CookieValue(value = "active_case") String c_id, ModelMap map,
                       @RequestParam(value = "error", required = false) Integer error_id) {

        final String return_address_error = "redirect:/welcome?error=1";

        if(c_id == null || c_id.isEmpty()) {
            return return_address_error;
        }

        // Write Error Code to ModelMap
        if(error_id != null && allowedErrors.contains(error_id)) {
            map.addAttribute("error", error_id);
        }

        int id;
        try {
            id = Integer.parseInt(c_id);
        } catch(NumberFormatException nfe) {
            logger.fine(nfe.getMessage());
            return return_address_error;
        }

        Concern concern = concernService.getById(id);
        //List<Unit> unit_list = unitService.getAll(id);
        //Set<Integer> nonDeletables = unitService.getNonDeletable(id);

        map.addAttribute("concern", concern);
        //map.addAttribute("unit_list", unit_list);

        //HashMap<Integer, Boolean> locked = new HashMap<Integer, Boolean>();
        //for(Unit u : unit_list) {
        //    locked.put(u.getId(), nonDeletables.contains(u.getId()));
        //}

        //map.addAttribute("locked", locked);

        return "edit_concern";
    }



    @RequestMapping("container")
    public String editContainer(@CookieValue(value = "active_case") String c_id, ModelMap map) {

        final String return_address_error = "redirect:/welcome?error=1";

        if(c_id == null || c_id.isEmpty()) {
            return return_address_error;
        }

        int id;
        try {
            id = Integer.parseInt(c_id);
        } catch(NumberFormatException nfe) {
            logger.fine(nfe.getMessage());
            return return_address_error;
        }

        Concern concern = concernService.getById(id);

        map.addAttribute("concern", concern);

        return "edit_container";
    }

}
