package at.wrk.coceso.controller;

import at.wrk.coceso.dao.PersonDao;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.service.OperatorService;
import at.wrk.coceso.service.PersonService;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/edit/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @Autowired
    OperatorService operatorService;

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
            Logger.debug(e.getMessage());
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
        if(person.id <= 0) {
            map.addAttribute("error", "Update of User Failed. Invalid ID: "+person.id);
            return "redirect:/edit/person";
        }
        if(!personService.update(person)) {
            map.addAttribute("error", "Something went wrong on Update...");   //TODO Error Message not shown
            return "redirect:/edit/person";
        }

        return "redirect:/edit/person/update?id="+person.id;
    }

    @RequestMapping(value = "updateOp", method = RequestMethod.POST)
    @PreAuthorize("hasRole('Root')")
    public String updateOpByPost(@ModelAttribute Operator operator, ModelMap map) {
        if(operator.id <= 0) {
            map.addAttribute("error", "Update of User Failed. Invalid ID: "+operator.id);
            return "redirect:/edit/person";
        }
        Operator op = operatorService.getById(operator.getId());
        if(op == null) {
            map.addAttribute("error", "Person not found: "+operator.id);
            return "redirect:/edit/person";
        }
        op.username = operator.username;
        op.allowLogin = operator.allowLogin;

        if(!operatorService.update(op)) {
            map.addAttribute("error", "Something went wrong on Update...");   //TODO Error Message not shown
            return "redirect:/edit/person";
        }

        return "redirect:/edit/person/update?id="+operator.id;
    }


    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@ModelAttribute Person person, ModelMap map) {
        person.id = personService.add(person);
        if(person.id == -1)
            map.addAttribute("error", "Something went wrong on Create...");  //TODO Error Message not shown

        return "redirect:/edit/person/update?id="+person.id;
    }

    @RequestMapping(value = "createOp", method = RequestMethod.POST)
    @PreAuthorize("hasRole('Root')")
    public String createOp(@ModelAttribute Operator operator, ModelMap map) {
        Operator op = new Operator(personService.getById(operator.id));
        op.allowLogin = operator.allowLogin;
        op.username = operator.username;

        int id = operatorService.add(operator);
        if(id != operator.getId())
            map.addAttribute("error", "Something went wrong on Create...");  //TODO Error Message not shown

        return "redirect:/edit/person/update?id="+operator.id;
    }
}
