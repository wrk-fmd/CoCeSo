package at.wrk.coceso.auth;

import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.helper.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JsonAuthError implements AuthenticationEntryPoint, AccessDeniedHandler {

  @Autowired
  private ObjectMapper objectMapper;

  // User not authenticated
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, new RestResponse(Errors.HttpUnauthorized));
  }

  // User not authorized
  @Override
  public void handle(HttpServletRequest requests, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
    sendError(response, HttpServletResponse.SC_FORBIDDEN, new RestResponse(Errors.HttpAccessDenied));
  }

  private void sendError(HttpServletResponse response, int code, RestResponse error) throws IOException {
    response.setStatus(code);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(error));
  }
}
