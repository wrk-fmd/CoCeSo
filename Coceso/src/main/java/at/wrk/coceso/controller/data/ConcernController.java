package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.service.ConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("/data/concern")
public class ConcernController {

    @Autowired
    ConcernService concernService;

    Logger logger = Logger.getLogger("CoCeSo");

    @ResponseBody
    @RequestMapping("get/{id}")
    public Concern get(@PathVariable("id") String s_id) {
        try {
            return concernService.getById(Integer.parseInt(s_id));
        } catch(NumberFormatException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody Concern concern, BindingResult result, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        logger.fine("Update request of Concern #" + concern.getId() + " by User " + user.getUsername());

        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        boolean success = concernService.update(concern, user);

        return "{\"success\":" + success + "," + (success ? "" : "\"error\":3") + "}";
    }

}
