package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.RoleDao;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.service.OperatorService;
import at.wrk.coceso.service.PersonService;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/edit/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private RoleDao roleDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(ModelMap map, HttpServletRequest request, @RequestParam(value = "error", required = false) String s_error) {

        if(s_error != null) {
            map.addAttribute("error", s_error);
        }

        map.addAttribute("persons", personService.getAll());

        if(request.isUserInRole("Root")) {
            map.addAttribute("operators", operatorService.getAll());
        }

        return "personmgmt";
    }

    @RequestMapping(value = "update", method = RequestMethod.GET)
    //@PreAuthorize("hasAnyRole('MLS', 'Root')")
    public String update(ModelMap map, @RequestParam(value = "id", required = false) String s_id,
                         HttpServletRequest request)
    {
        if(s_id == null) {
           return "redirect:/edit/person";
        }
        int id;
        try {
            id = Integer.parseInt(s_id);
        } catch (Exception e) {
            CocesoLogger.debug(e.getMessage());
            return "redirect:/edit/person";
        }
        Person p = personService.getById(id);

        if(p == null) {
            map.addAttribute("error", "no_user_found");
            return "redirect:/edit/person";
        }

        map.addAttribute("p_person", p);

        if(request.isUserInRole("Root")) {
            Operator op = operatorService.getById(p.getId());

            map.addAttribute("authorities", CocesoAuthority.class.getEnumConstants());

            if(op == null) {
                map.addAttribute("user_not_op", true);
            }
            else {
                map.addAttribute("operator", op);
            }
        }

        return "edit_person";

    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    //@PreAuthorize("hasAnyRole('MLS', 'Root')")
    public String updateByPost(@ModelAttribute Person person, ModelMap map) {
        if(person.getId() <= 0) {
            map.addAttribute("error", "Update of User Failed. Invalid ID: "+ person.getId());
            return "redirect:/edit/person";
        }
        if(!personService.update(person)) {
            map.addAttribute("error", "Something went wrong on Update...");   //TODO Error Message not shown
            return "redirect:/edit/person";
        }

        return "redirect:/edit/person/update?id="+ person.getId();
    }

    @RequestMapping(value = "updateOp", method = RequestMethod.POST)
    @PreAuthorize("hasRole('Root')")
    public String updateOpByPost(@ModelAttribute Operator operator, ModelMap map, HttpServletRequest request,
                                 Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        if(operator.getId() <= 0) {
            map.addAttribute("error", "Update of User Failed. Invalid ID: "+ operator.getId());
            return "redirect:/edit/person";
        }
        Operator op = operatorService.getById(operator.getId());
        if(op == null) {
            map.addAttribute("error", "Person not found: "+ operator.getId());
            return "redirect:/edit/person";
        }
        CocesoLogger.info("Updating Operator " + op.getUsername() + "(#" + op.getId() + ") by " + user.getUsername());

        op.setUsername(operator.getUsername());
        op.setAllowLogin(operator.isAllowLogin());

        List<CocesoAuthority> new_authorities = operator.getInternalAuthorities();
        List<CocesoAuthority> old_authorities = op.getInternalAuthorities();
        if(new_authorities != null) {
            CocesoLogger.debug("PersonController.updateOpByPost: updating Authorities of " + op.getUsername());
            for(CocesoAuthority auth : CocesoAuthority.class.getEnumConstants()) {
                if(new_authorities.contains(auth) && !old_authorities.contains(auth))
                    roleDao.add(operator.getId(), auth);
                if(!new_authorities.contains(auth) && old_authorities.contains(auth)) {
                    roleDao.remove(operator.getId(), auth);
                }
            }
        } else {
            CocesoLogger.debug("PersonController.updateOpByPost: deleting all Authorities of " + op.getUsername());
            for(CocesoAuthority auth : old_authorities) {
                roleDao.remove(operator.getId(), auth);
            }
        }

        if(!operatorService.update(op)) {
            map.addAttribute("error", "Something went wrong on Update...");   //TODO Error Message not shown
            return "redirect:/edit/person";
        }

        return "redirect:/edit/person/update?id="+ operator.getId();
    }


    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@ModelAttribute Person person, ModelMap map) {
        person.setId(personService.add(person));
        if(person.getId() == -1)
            map.addAttribute("error", "Something went wrong on Create...");  //TODO Error Message not shown

        return "redirect:/edit/person/update?id="+ person.getId();
    }

    @RequestMapping(value = "createOp", method = RequestMethod.POST)
    @PreAuthorize("hasRole('Root')")
    public String createOp(@ModelAttribute Operator operator, ModelMap map) {
        Operator op = new Operator(personService.getById(operator.getId()));
        op.setAllowLogin(operator.isAllowLogin());
        op.setUsername(operator.getUsername());

        int id = operatorService.add(operator);
        if(id != operator.getId())
            map.addAttribute("error", "Something went wrong on Create...");  //TODO Error Message not shown

        return "redirect:/edit/person/update?id="+ operator.getId();
    }
}
