package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.service.ConcernService;
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

  private static final String error_return = "redirect:/home?error=1";

  @Autowired
  ConcernService concernService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String editConcern(ModelMap model, @CookieValue(value = "concern", required = false) Integer concern_id) {
    if (concern_id == null) {
      return error_return;
    }

    Concern concern = concernService.getById(concern_id);
    if (concern == null || concern.isClosed()) {
      return error_return;
    }

    model.addAttribute("concern", concern);

    return "edit_concern";
  }

  @RequestMapping(value = "container", method = RequestMethod.GET)
  public String editContainer(ModelMap model, @CookieValue(value = "concern", required = false) Integer concern_id) {
    if (concern_id == null) {
      return error_return;
    }

    Concern concern = concernService.getById(concern_id);
    if (concern == null || concern.isClosed()) {
      return error_return;
    }

    model.addAttribute("concern", concern);

    return "edit_container";
  }

  @RequestMapping(value = "person", method = RequestMethod.GET)
  public String editPerson(ModelMap model, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    if (user.hasAuthority(CocesoAuthority.Root)) {
      model.addAttribute("authorities", CocesoAuthority.class.getEnumConstants());
      model.addAttribute("authorized", true);
    }
    return "edit_person";
  }

}
