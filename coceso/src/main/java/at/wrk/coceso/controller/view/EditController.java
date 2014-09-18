package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.service.ConcernService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/edit")
public class EditController {

    private final static
    Logger LOG = Logger.getLogger(EditController.class);

    private static final String error_return = "redirect:/home?error=1";

    @Autowired
    private ConcernService concernService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String editConcern(ModelMap model, @CookieValue(value = "concern", required = false) Integer concern_id,
                              UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        if (concern_id == null) {
            LOG.info("tried to edit concern without concern ID");
            return error_return;
        }

        Concern concern = concernService.getById(concern_id);
        if(concern == null) {
            LOG.info("concern doesn't exist");
            return error_return;
        }

        if (concern.isClosed()) {
            LOG.info("concern already closed, edit not allowed");
            return error_return;
        }

        LOG.info(String.format("User %s started edit of Concern #%d (%s)",
                user == null ? "N/A" : user.getUsername(), concern.getId(), concern.getName()));

        model.addAttribute("concern", concern);

        return "edit_concern";
    }

    @RequestMapping(value = "container", method = RequestMethod.GET)
    public String editContainer(ModelMap model, @CookieValue(value = "concern", required = false) Integer concern_id) {
        if (concern_id == null) {
            LOG.info("no concern_id provided");
            return error_return;
        }

        Concern concern = concernService.getById(concern_id);
        if (concern == null || concern.isClosed()) {
            LOG.info("concern non existent or closed");
            return error_return;
        }

        model.addAttribute("concern", concern);

        return "edit_container";
    }

    @RequestMapping(value = "person", method = RequestMethod.GET)
    public String editPerson(ModelMap model, UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();
        if (user.hasAuthority(CocesoAuthority.Root)) {
            LOG.debug("authority granted");
            model.addAttribute("authorities", CocesoAuthority.class.getEnumConstants());
            model.addAttribute("authorized", true);
        }
        return "edit_person";
    }

}
