package at.wrk.coceso.validator.impl;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
class UserValidatorImpl implements UserValidator {

  @Autowired
  UserService userService;

  @Override
  public boolean supports(Class<?> type) {
    return User.class.equals(type);
  }

  @Override
  public void validate(final Object o, final Errors errors) {
    User u = (User) o;

    // Check if user really exists
    if (u.getId() != null) {
      User old = userService.getById(u.getId());
      if (old == null) {
        errors.reject("user.missing");
        return;
      }
    }

    // Check if username is unique
    User existing = userService.getByUsername(u.getUsername());
    if (existing != null && !u.equals(existing)) {
      errors.rejectValue("name", "user.username.exists");
    }
  }
}
