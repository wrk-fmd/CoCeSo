package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.PasswordForm;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.validator.PasswordValidator;
import at.wrk.coceso.validator.UserValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("@auth.hasAccessLevel('Edit')")
@RequestMapping("/data/user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserValidator userValidator;

  @Autowired
  private PasswordValidator passwordValidator;

  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public List<User> getAll(@AuthenticationPrincipal User user) {
    // TODO Access level
    return userService.getAll();
  }

  @RequestMapping(value = "getFiltered", produces = "application/json", method = RequestMethod.GET)
  public Page<User> getAll(Pageable pageable, @RequestParam(value = "filter", required = false) String filter) {
    // TODO Access level
    return userService.getAll(pageable, filter);
  }

  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  public User getById(@PathVariable("id") int id, @AuthenticationPrincipal User user) {
    // TODO Access level
    return userService.getById(id);
  }

  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public RestResponse update(@RequestBody @Validated User editedUser, BindingResult result,
      @AuthenticationPrincipal User user) {
    userValidator.validate(user, result);
    if (result.hasErrors()) {
      return new RestResponse(result);
    }

    editedUser = userService.update(editedUser, user);
    return new RestResponse(true, new RestProperty("id", editedUser.getId()));
  }

  @PreAuthorize("@auth.hasAccessLevel('Root')")
  @RequestMapping(value = "setPassword", produces = "application/json", method = RequestMethod.POST)
  public RestResponse setPassword(@RequestBody @Validated PasswordForm form,
      BindingResult result, @AuthenticationPrincipal User user) {
    passwordValidator.validate(form, result);
    if (result.hasErrors()) {
      return new RestResponse(result);
    }

    return new RestResponse(userService.setPassword(form, user));
  }

  @RequestMapping(value = "upload", produces = "application/json",
      consumes = "text/csv", method = RequestMethod.POST)
  public RestResponse upload(@RequestBody String body, @AuthenticationPrincipal User user) {
    int ret = userService.importUsers(body, user);
    if (ret < 0) {
      return new RestResponse(false);
    }
    return new RestResponse(true, new RestProperty("counter", ret));
  }

}
