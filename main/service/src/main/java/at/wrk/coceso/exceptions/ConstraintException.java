package at.wrk.coceso.exceptions;

import at.wrk.coceso.entity.enums.Errors;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ConstraintException extends ErrorsException {

  private final Set<ConstraintViolation<Object>> violations;

  public ConstraintException(Set<ConstraintViolation<Object>> violations) {
    super(Errors.Constraint);
    this.violations = violations;
  }

  public Set<ConstraintViolation<Object>> getViolations() {
    return violations;
  }

}
