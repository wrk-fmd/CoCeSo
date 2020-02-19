package at.wrk.coceso.validator.impl;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.PasswordForm;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.validator.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
class PasswordValidatorImpl implements PasswordValidator {

  private final static int MINLENGTH = 6;

  @Autowired
  private UserService userService;

  @Override
  public boolean supports(Class<?> type) {
    return PasswordForm.class.equals(type) || String.class.equals(type);
  }

  @Override
  public void validate(Object o, Errors errors) {
    String p = null;
    if (o instanceof String) {
      p = (String) o;
    } else if (o instanceof PasswordForm) {
      PasswordForm f = (PasswordForm) o;

      // Check if user really exists
      User old = userService.getById(f.getId());
      if (old == null) {
        errors.reject("password.user.missing");
        return;
      }

      // Check if user matches
      if (old.getUsername() == null || !old.getUsername().equals(f.getUsername())) {
        errors.reject("password.user.mismatch");
      }

      p = f.getPassword();
    }

    if (p == null || p.trim().isEmpty()) {
      errors.reject("password.empty");
      return;
    }

    if (p.length() < MINLENGTH) {
      errors.reject("password.short");
    }
  }

}
