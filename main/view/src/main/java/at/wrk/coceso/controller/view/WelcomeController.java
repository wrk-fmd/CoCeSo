package at.wrk.coceso.controller.view;

import at.wrk.coceso.controller.config.DeploymentStatusProvider;
import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.utils.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final static Logger LOG = LoggerFactory.getLogger(WelcomeController.class);

    private static final Set<Integer> ALLOWED_ERRORS = Set.of(1);

    private final UserService userService;
    private final DeploymentStatusProvider deploymentStatusProvider;
    private final String publicGeoBrokerUrl;

    @Autowired
    public WelcomeController(
            final UserService userService,
            final DeploymentStatusProvider deploymentStatusProvider,
            final @Value("${geobroker.public.url:configuration-missing}") String publicGeoBrokerUrl) {
        this.userService = userService;
        this.deploymentStatusProvider = deploymentStatusProvider;
        this.publicGeoBrokerUrl = publicGeoBrokerUrl.endsWith("/") ? publicGeoBrokerUrl : publicGeoBrokerUrl + "/";
    }

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
            @AuthenticationPrincipal final AuthenticatedUser authenticationUser,
            final HttpServletResponse response,
            @RequestParam(value = "error", required = false) final Integer error) {
        map.addAttribute("error", error != null && ALLOWED_ERRORS.contains(error) ? error : 0);

        User user = userService.getById(authenticationUser.getUserId());

        // Read last active Concern
        Concern active = user.getActiveConcern();

        // Check if still active and valid
        if (!Concern.isClosedOrNull(active)) {
            response.addCookie(new Cookie("concern", active.getId() + ""));
        } else {
            // Delete Cookie and active concern reference
            LOG.info("{}: Active concern is already closed, cleaning up cookie", authenticationUser);
            response.addCookie(new Cookie("concern", null));
            userService.setActiveConcern(authenticationUser, null);
        }

        // Add Userdetails to Model
        map.addAttribute("user", Initializer.init(user, User::getInternalAuthorities));
        map.addAttribute("authenticatedUser", authenticationUser);
        map.put("isGeoBrokerFeatureAvailable", deploymentStatusProvider.isGeoBrokerModuleDeployed());

        LOG.debug("{}: Started Home Screen with model map: {}", user, map);

        return "home";
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "/geo/qr-codes", method = RequestMethod.GET)
    public String showQrCodePage(final ModelMap map, @RequestParam(value = "concernId") final int concernId) {
        map.put("concernId", concernId);
        map.put("publicGeobrokerUrl", publicGeoBrokerUrl);
        return "qr_codes";
    }
}
