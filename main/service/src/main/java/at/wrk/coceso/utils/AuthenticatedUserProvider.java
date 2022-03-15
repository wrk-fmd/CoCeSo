package at.wrk.coceso.utils;

import at.wrk.coceso.data.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Optional;

@Component
public class AuthenticatedUserProvider {

    /**
     * Returns the authenticated user of the current context. Returns {@literal null} if no user is logged in or found in context.
     */
    @Nullable
    public AuthenticatedUser getAuthenticatedUser() {
        return (AuthenticatedUser) Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(x -> x instanceof AuthenticatedUser)
                .orElse(null);
    }
}
