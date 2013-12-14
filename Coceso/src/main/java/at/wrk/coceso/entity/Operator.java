package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.CocesoAuthority;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Operator extends Person implements UserDetails {

    public Concern activeConcern;

    @JsonIgnore
    public boolean allowLogin;

    @JsonIgnore
    public String hashedPW;

    @JsonIgnore
    public String username;

    @JsonIgnore
    private List<CocesoAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> ret = new ArrayList<GrantedAuthority>();

        if(authorities != null) for(final CocesoAuthority auth : authorities) {
            ret.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return auth.name();
                }
                @Override
                public String toString() {
                    return auth.name();
                }
            });
        }
        return ret;
    }

    public void setAuthorities(List<CocesoAuthority> grantedAuthorities) {
        authorities = grantedAuthorities;

    }

    public void addAuthority(CocesoAuthority authority) {
        authorities.add(authority);
    }

    public boolean removeAuthority(CocesoAuthority authority) {
        return authorities.remove(authority);
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return hashedPW;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return allowLogin;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return allowLogin;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return allowLogin;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return allowLogin;
    }

    public boolean validatePassword(String pw) {
        if(hashedPW == null || hashedPW.isEmpty()) return false;
        PasswordEncoder enc = new BCryptPasswordEncoder();
        return enc.matches(pw, hashedPW);
    }

    public void setPassword(String pw) {
        PasswordEncoder enc = new BCryptPasswordEncoder();
        hashedPW = enc.encode(pw);
    }
}
