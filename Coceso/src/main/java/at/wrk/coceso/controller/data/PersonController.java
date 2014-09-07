package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.RoleDao;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.service.OperatorService;
import at.wrk.coceso.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/data/person")
public class PersonController {

  private static final Logger logger = Logger.getLogger("CoCeSo");

  @Autowired
  PersonService personService;

  @Autowired
  OperatorService operatorService;

  @Autowired
  RoleDao roleDao;

  @ResponseBody
  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public List<? extends Person> getAll(UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    return personService.getAll(user.hasAuthority(CocesoAuthority.Root));
  }

  @ResponseBody
  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  public Person getById(@PathVariable("id") int id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    Person person = null;
    if (user.hasAuthority(CocesoAuthority.Root)) {
      person = operatorService.getById(id);
    }
    if (person == null) {
      person = personService.getById(id);
    }
    return person;
  }

  @ResponseBody
  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public String update(@RequestBody Operator operator, BindingResult result, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    if (operator.getId() <= 0) {
      operator.setId(personService.add(operator));

      if (operator.getId() <= 0) {
        return "{\"success\":false,\"error\":\"addperson\"}";
      }
      if (user.hasAuthority(CocesoAuthority.Root) && operator.getUsername() != null) {
        return updateOp(operator, user);
      }
      return "{\"success\":true,\"id\":" + operator.getId() + "}";
    }

    if (!personService.update(operator)) {
      return "{\"success\":false,\"error\":\"updateperson\"}";
    }
    if (user.hasAuthority(CocesoAuthority.Root) && operator.getUsername() != null) {
      return updateOp(operator, user);
    }

    return "{\"success\":true}";
  }

  private String updateOp(Operator operator, Operator user) {
    int id = operator.getId();
    if (id <= 0) {
      //Is checked in calling method, catch it anyway
      return "{\"success\":false}";
    }

    Operator op = operatorService.getById(id);
    if (op == null) {
      Person person = personService.getById(id);
      if (person == null) {
        //Is also checked in calling method
        return "{\"success\":false}";
      }

      op = new Operator(person);
      op.setAllowLogin(operator.isAllowLogin());
      op.setUsername(operator.getUsername());
      op.setInternalAuthorities(new ArrayList<CocesoAuthority>());

      logger.log(Level.INFO, "Creating Operator {0}(#{1}) by {2}", new Object[]{op.getUsername(), op.getId(), user.getUsername()});

      if (operatorService.add(op) != id) {
        return "{\"success\":false,\"error\":\"addop\",\"id\":" + id + "}";
      }
    } else {
      logger.log(Level.INFO, "Updating Operator {0}(#{1}) by {2}", new Object[]{op.getUsername(), op.getId(), user.getUsername()});

      op.setAllowLogin(operator.isAllowLogin());
      op.setUsername(operator.getUsername());

      if (!operatorService.update(op)) {
        return "{\"success\":false,\"error\":\"updateop\",\"id\":" + id + "}";
      }
    }

    //Update authorities
    List<CocesoAuthority> new_authorities = operator.getInternalAuthorities();
    List<CocesoAuthority> old_authorities = op.getInternalAuthorities();
    if (new_authorities != null) {
      logger.log(Level.FINE, "Updating Authorities of {0} by {1}", new Object[]{op.getUsername(), user.getUsername()});
      for (CocesoAuthority auth : CocesoAuthority.class.getEnumConstants()) {
        if (new_authorities.contains(auth) && !old_authorities.contains(auth)) {
          roleDao.add(id, auth);
        } else if (!new_authorities.contains(auth) && old_authorities.contains(auth)) {
          if (op.getId() == user.getId() && auth == CocesoAuthority.Root) {
            logger.log(Level.WARNING, "Prevented removal of own root authority by {0}", op.getUsername());
          } else {
            roleDao.remove(id, auth);
          }
        }
      }
    }

    return "{\"success\":true,\"id\":" + id + "}";
  }

}
