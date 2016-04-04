package at.wrk.coceso.controller.handler;

import at.wrk.coceso.exceptions.ConcernException;
import at.wrk.coceso.exceptions.NotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HtmlExceptionHandler {

  private final static Logger LOG = LoggerFactory.getLogger(HtmlExceptionHandler.class);

  @ExceptionHandler(ConcernException.class)
  protected String concernHandler(ConcernException e) {
    LOG.info("Redirecting to home: {}", e.getMessage());
    return "redirect:/home?error=1";
  }

  @ExceptionHandler(NotFoundException.class)
  protected void notFoundHandler(NotFoundException e, HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
  }
}
