package at.wrk.coceso.auth;

import at.wrk.coceso.config.AuthConfig;
import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Authority;
import at.wrk.coceso.service.UserService;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
class BasicAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BasicAuthenticationProvider.class);

    private final AuthConfig config;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public BasicAuthenticationProvider(final AuthConfig config, final UserService userService, final BCryptPasswordEncoder passwordEncoder) {
        this.config = config;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authenticationToken) throws AuthenticationException {
      AuthenticatedUser user = (AuthenticatedUser) userDetails;


        String username = user.getUsername();
        String password = (String) authenticationToken.getCredentials();

        if (StringUtils.isNotBlank(user.getPassword()) && passwordEncoder.matches(password, user.getPassword())) {
            LOG.info("[ OK ] {}: Offline authentication", username);
            return;
        }

        LOG.info("[failed] {}: Offline authentication", username);

        boolean success = false;
        if (config.useAuthUrl()) {
            LOG.info("{}: Attempting online authentication", username);
            try { // #################### THIRD PARTY AUTHENTICATION ####################
                String basicAuthenticationString = createBasicAuthenticationString(username, password);

                HttpURLConnection connection = (HttpURLConnection) config.getAuthUrl().openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(false);
                connection.setRequestProperty("Authorization", basicAuthenticationString);

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
            LOG.warn("[ OK ] {}: 'First use' authentication is enabled. User is logged in without password check.", username);
            success = true;
        }

        if (success) {
            userService.setPassword(user.getUserId(), password);
            LOG.info("User {}: PW hash for offline authentication was written to DB.", username);
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    private String createBasicAuthenticationString(final String username, final String password) {
        String phrase = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(phrase.getBytes());
        return "Basic " + encoded;
    }

    @Override
    protected AuthenticatedUser retrieveUser(String username, UsernamePasswordAuthenticationToken userPasswordAuthenticationToken) throws AuthenticationException {
        User user = userService.getByUsername(username);
        if (user == null) {
            LOG.info("[failed] {}: User not found", username);
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }

        Set<Authority> authorities = Optional.ofNullable(user.getInternalAuthorities())
                .orElse(ImmutableSet.of())
                .stream()
                .flatMap(authority -> authority.getAuthorities().stream())
                .collect(toSet());
        String displayName = user.getFirstname() + " " + user.getLastname();
        return new AuthenticatedUser(user.getId(), user.getUsername(), displayName, user.getPassword(), user.isAllowLogin(), authorities);
    }
}
