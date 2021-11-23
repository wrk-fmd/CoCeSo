package at.wrk.coceso.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthenticatedUser {

    /**
     * Returns the authenticated user of the current context. Returns {@literal null} if no user is logged in or found in context.
     */
    public static String getName() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(Object::toString)
                .orElse(null);
    }
}
