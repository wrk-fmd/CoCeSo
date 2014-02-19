package at.wrk.coceso.controller.view;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.OperatorDao;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Controller
public class WelcomeController {

    // TODO Authority to Close and Reopen Concerns
    private static final CocesoAuthority CLOSE_AUTHORITY = CocesoAuthority.Root;

    private static Set<Integer> allowedErrors;

    static {
        allowedErrors = new HashSet<Integer>();
        allowedErrors.add(1);
    }

    @Autowired
    ConcernDao concernDao;

    @Autowired
    OperatorDao operatorDao;

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

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String showWelcome(ModelMap map, Principal principal, HttpServletResponse response,
                              @RequestParam(value = "error", required = false) Integer id) {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        // Write Error Code to ModelMap
        if(id != null && allowedErrors.contains(id)) {
            map.addAttribute("error", id);
        }

        user = operatorDao.getById(user.getId());

        // Read last active Concern
        Concern active = user.getActiveConcern();
        // Check if still active and valid
        if(active != null && !active.isClosed()) {
            response.addCookie(new Cookie("active_case", active.getId()+""));
            map.addAttribute("activeConcern", active);
        } else{
            // Delete Cookie
            response.addCookie(new Cookie("active_case", null));
            // Delete Active Case Reference
            user.setActiveConcern(null);
            operatorDao.update(user);
        }

        // Add Userdetails to Model
        map.addAttribute("user", user);

        List<Concern> concern_list = new LinkedList<Concern>();
        List<Concern> closed_concern_list = new LinkedList<Concern>();

        // Divide the Concerns in Closed/Open
        for(Concern concern : concernDao.getAll()) {
            if(concern.isClosed()) {
                closed_concern_list.add(concern);
            }
            else {
                concern_list.add(concern);
            }
        }

        map.addAttribute("concern_list", concern_list);
        map.addAttribute("closed_concern_list", closed_concern_list);

        // Flag Authorized toggles visibility of close/reopen button
        if(user.getInternalAuthorities().contains(CLOSE_AUTHORITY))
            map.addAttribute("authorized", true);

        //Logger.debug("ConcernList: size active:" + concern_list.size()+", size closed:" + closed_concern_list.size());

        return "welcome";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.POST)
    public String welcomeRedirect(HttpServletRequest request, HttpServletResponse response, Principal principal) {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        user = operatorDao.getById(user.getId());

        int case_id;
        try {
            case_id = Integer.parseInt(request.getParameter("case_id"));
        } catch (NumberFormatException e) {
            return "redirect:/welcome";
        }

        Concern currentConcern = concernDao.getById(case_id);

        // Not existent
        if(currentConcern == null) {
            return "redirect:/welcome";
        }


        if(!currentConcern.isClosed()) {
            if(request.getParameter("close") != null) {
                if(user.getInternalAuthorities().contains(CLOSE_AUTHORITY)){
                    currentConcern.setClosed(true);
                    concernDao.update(currentConcern);
                }
                else {
                    Logger.error("User "+user.getUsername()+" tried to close Concern \"" + currentConcern.getName() +
                            "\" without Authority \"" + CLOSE_AUTHORITY + "\"");
                }
            }
            else {
                user.setActiveConcern(currentConcern);
                operatorDao.update(user);

                response.addCookie(new Cookie("active_case", case_id+""));

                if(request.getParameter("start") != null)
                    return "redirect:/main";
                if(request.getParameter("edit") != null)
                    return "redirect:/edit";
            }
        }
        else {
            if(request.getParameter("print") != null)
                return "redirect:/finalReport/report.pdf?id="+currentConcern.getId();

            if(request.getParameter("reopen") != null)  {
                if(user.getInternalAuthorities().contains(CLOSE_AUTHORITY)){
                    currentConcern.setClosed(false);
                    concernDao.update(currentConcern);
                }
                else {
                    Logger.error("User "+user.getUsername()+" tried to reopen Concern \"" + currentConcern.getName() +
                            "\" without Authority \"" + CLOSE_AUTHORITY + "\"");
                }
            }
        }

        return "redirect:/welcome";
    }
}