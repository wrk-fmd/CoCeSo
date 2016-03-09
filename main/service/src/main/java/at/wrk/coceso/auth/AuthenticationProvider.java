package at.wrk.coceso.auth;

import at.wrk.coceso.config.AuthConfig;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.entity.User;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProvider.class);

  @Autowired
  private AuthConfig config;

  @Autowired
  private UserService userService;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken upat) throws AuthenticationException {
    User user = (User) userDetails;
    String username = user.getUsername(), password = (String) upat.getCredentials();

    if (user.validatePassword(password)) {
      LOG.info("[ OK ] {}: Offline authentication", username);
      return;
    }
    LOG.info("[failed] {}: Offline authentication", username);

    boolean success = false;
    if (config.useAuthUrl()) {
      LOG.info("{}: Attempting online authentication", username);
      try { // #################### THIRD PARTY AUTHENTICATION ####################
        String phrase = username + ":" + password;
        String encoded = new String(Base64.encode(phrase.getBytes()));

        HttpURLConnection connection = (HttpURLConnection) config.getAuthUrl().openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Authorization", "Basic " + encoded);

        int returnCode = connection.getResponseCode();
        connection.disconnect();

        switch (returnCode) {
          case AuthConfig.SUCCESS_CODE:
            LOG.info("[ OK ] {}: Online authentication", username);
            success = true;
            break;
          case AuthConfig.FAILURE_CODE:
            LOG.info("[failed] {}: Online authentication", username);
            break;
          default:
            LOG.warn("[failed] {}: Online authentication, unexpected error code {}", username, returnCode);
            break;
        }
      } catch (IOException e) {
        LOG.warn("[failed] {}: Online authentication, failed with exception {}", username, e.getMessage());
      }
    } else if (config.isFirstUse()) {
      LOG.info("[ OK ] {}: First use authentication", username);
      success = true;
    }

    if (success) {
      userService.setPassword(user.getId(), password, user);
      LOG.info("User {}: PW written to DB", username);
      return;
    }
    throw new BadCredentialsException("Bad credentials");
  }

  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken upat) throws AuthenticationException {
    User user = userService.getByUsername(username);
    if (user == null) {
      LOG.info("[failed] {}: User not found", username);
      throw new UsernameNotFoundException(String.format("User '%s' not found", username));
    }
    return user;
  }
}
