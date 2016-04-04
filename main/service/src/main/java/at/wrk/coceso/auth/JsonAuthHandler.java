package at.wrk.coceso.auth;

import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class JsonAuthHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication auth) throws ServletException, IOException {
    HttpSession session = request.getSession();
    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    response.getWriter().write(objectMapper.writeValueAsString(new RestResponse(true,
        new RestProperty("JSESSIONID", session.getId()))));
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException ex) throws ServletException, IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(new RestResponse(false)));
  }

}
