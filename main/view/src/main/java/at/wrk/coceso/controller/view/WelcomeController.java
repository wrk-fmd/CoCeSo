package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WelcomeController {

  private static final Set<Integer> allowedErrors = new HashSet<>(Arrays.asList(1));

  private final static Logger LOG = LoggerFactory.getLogger(WelcomeController.class);

  @Autowired
  private UserService userService;

  @PreAuthorize("permitAll")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String showIndex() {
    return "index";
  }

  @PreAuthorize("permitAll")
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(HttpServletRequest request, ModelMap map) {
    HttpSession session = request.getSession(false);
    if (session != null && session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null) {
      map.addAttribute("error", true);
      session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
    return "login";
  }

  @PreAuthorize("isAuthenticated()")
  @RequestMapping(value = "/home", method = RequestMethod.GET)
  public String showHome(ModelMap map, @AuthenticationPrincipal User user, HttpServletResponse response,
          @RequestParam(value = "error", required = false) Integer error) {
    map.addAttribute("error", error != null && allowedErrors.contains(error) ? error : 0);

    user = userService.getById(user.getId());

    // Read last active Concern
    Concern active = user.getActiveConcern();

    // Check if still active and valid
    if (!Concern.isClosed(active)) {
      response.addCookie(new Cookie("concern", active.getId() + ""));
    } else {
      // Delete Cookie and active concern reference
      LOG.info("{}: Active concern already closed, clean up", user);
      response.addCookie(new Cookie("concern", null));
      userService.setActiveConcern(user, null);
    }

    // Add Userdetails to Model
    map.addAttribute("user", user);

    LOG.debug("{}: Started Home Screen", user);

    return "home";
  }

}
