package at.wrk.coceso.controller.handler;

import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.exceptions.ConcernException;
import at.wrk.coceso.exceptions.ConstraintException;
import at.wrk.coceso.exceptions.ErrorsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
@Order(1)
public class JsonExceptionHandler {

  private final static Logger LOG = LoggerFactory.getLogger(JsonExceptionHandler.class);

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ConcernException.class)
  @ResponseBody
  protected RestResponse concernHandler(ConcernException e) {
    LOG.info("ConcernException: {}", e.getMessage());
    return new RestResponse(Errors.ConcernMissingOrClosed);
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(ErrorsException.class)
  @ResponseBody
  protected RestResponse errorsHandler(ErrorsException e) {
    LOG.info("ErrorsException: {}", e.getMessage());
    return new RestResponse(e.getErrors());
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(ConstraintException.class)
  @ResponseBody
  protected RestResponse constraintHandler(ConstraintException e) {
    LOG.info("ErrorsException: {}", e.getMessage());
    return new RestResponse(e.getErrors(), new RestProperty("violations", e.getViolations()));
  }

}
