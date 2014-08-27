package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
public class WelcomeController {
  // TODO Authority to Close and Reopen Concerns
  private static final CocesoAuthority CLOSE_AUTHORITY = CocesoAuthority.Root;

  private static final Set<Integer> allowedErrors = new HashSet<Integer>(Arrays.asList(1));

  @Autowired
  OperatorService operatorService;

  @RequestMapping("/")
  public String showIndex() {
    return "index";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping("/loginfailed")
  public String loginFailed(ModelMap model) {
    model.addAttribute("error", "true");
    return "login";
  }

  @RequestMapping(value = "/home", method = RequestMethod.GET)
  public String showHome(ModelMap map, UsernamePasswordAuthenticationToken token, HttpServletResponse response,
          @RequestParam(value = "error", required = false) Integer error) {
    Operator user = (Operator) token.getPrincipal();

    // Write Error Code to ModelMap
    if (error != null && allowedErrors.contains(error)) {
      map.addAttribute("error", error);
    }

    user = operatorService.getById(user.getId());

    // Read last active Concern
    Concern active = user.getActiveConcern();
    // Check if still active and valid
    if (active != null && !active.isClosed()) {
      response.addCookie(new Cookie("concern", active.getId() + ""));
    } else {
      // Delete Cookie
      response.addCookie(new Cookie("concern", null));
      // Delete Active Concern Reference
      user.setActiveConcern(null);
      operatorService.update(user);
    }

    // Add Userdetails to Model
    map.addAttribute("user", user);

    // Flag Authorized toggles visibility of close/reopen button
    if (user.getInternalAuthorities().contains(CLOSE_AUTHORITY)) {
      map.addAttribute("authorized", true);
    }

    return "home";
  }

}
