package at.wrk.coceso.controller.view;

import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.utils.ActiveConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("@auth.hasAccessLevel('Edit')")
@RequestMapping("/edit")
public class EditController {

    private final static Logger LOG = LoggerFactory.getLogger(EditController.class);

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String editConcern(@ActiveConcern final Concern concern, @AuthenticationPrincipal final AuthenticatedUser user) {
        LOG.info("User {} started editing of concern {}.", user, concern);
        return "edit_concern";
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public String editUser() {
        return "edit_user";
    }
}
