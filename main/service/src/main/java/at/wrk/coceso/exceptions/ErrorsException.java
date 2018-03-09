package at.wrk.coceso.exceptions;

import at.wrk.coceso.entity.enums.Errors;

public class ErrorsException extends RuntimeException {

  private final Errors errors;

  public ErrorsException(Errors errors) {
    super(errors.getMessage());
    this.errors = errors;
  }

  public ErrorsException(Errors errors, Throwable cause) {
    super(errors.getMessage(), cause);
    this.errors = errors;
  }

  public Errors getErrors() {
    return errors;
  }

}
