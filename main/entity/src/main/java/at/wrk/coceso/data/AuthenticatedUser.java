package at.wrk.coceso.data;

import at.wrk.coceso.entity.enums.Authority;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class AuthenticatedUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final int userId;
    private final String username;
    private final String displayName;
    private final String encodedPassword;
    private final boolean isEnabled;
    private final Set<Authority> grantedAuthorities;

    public AuthenticatedUser(
            final int userId, final String username,
            final String displayName,
            final String encodedPassword,
            final boolean isEnabled,
            final Set<Authority> grantedAuthorities) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.encodedPassword = encodedPassword;
        this.isEnabled = isEnabled;
        this.grantedAuthorities = ImmutableSet.copyOf(grantedAuthorities);
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public Collection<Authority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "AuthenticatedUser:" + userId + "[" + username + "]";
    }
}
