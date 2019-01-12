package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.utils.Initializer;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

@Controller
public class WelcomeController {

    private static final Set<Integer> ALLOWED_ERRORS = ImmutableSet.of(1);

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
    public String login(final HttpServletRequest request, final ModelMap map) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null) {
            map.addAttribute("error", true);
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

        return "login";
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String showHome(
            final ModelMap map,
            @AuthenticationPrincipal final User authenticationUser,
            final HttpServletResponse response,
            @RequestParam(value = "error", required = false) final Integer error) {
        map.addAttribute("error", error != null && ALLOWED_ERRORS.contains(error) ? error : 0);

        User user = userService.getById(authenticationUser.getId());

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
        map.addAttribute("user", Initializer.init(user, User::getInternalAuthorities));

        LOG.debug("{}: Started Home Screen", user);

        return "home";
    }
}
