package at.wrk.coceso.validator.impl;

import at.wrk.coceso.exceptions.ConstraintException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class BeanValidator {

  @Autowired
  private Validator validator;

  public void validate(Object o) throws ConstraintException {
    Set<ConstraintViolation<Object>> violations = validator.validate(o);
    if (violations.size() > 0) {
      throw new ConstraintException(violations);
    }
  }

}
