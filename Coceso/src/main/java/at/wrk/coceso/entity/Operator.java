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

    private Concern activeConcern;

    @JsonIgnore
    private boolean allowLogin;

    @JsonIgnore
    private String hashedPW;

    private String username;

    public Operator() {
        super();
    }

    public Operator(Person person) {
        super(person);
    }

    @JsonIgnore
    private List<CocesoAuthority> internalAuthorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> ret = new ArrayList<GrantedAuthority>();

        if(internalAuthorities != null) for(final CocesoAuthority auth : internalAuthorities) {
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

    @JsonIgnore
    public List<CocesoAuthority> getInternalAuthorities() {
        return internalAuthorities;
    }

    public void setInternalAuthorities(List<CocesoAuthority> grantedAuthorities) {
        internalAuthorities = grantedAuthorities;

    }

    public void addAuthority(CocesoAuthority authority) {
        internalAuthorities.add(authority);
    }

    public boolean removeAuthority(CocesoAuthority authority) {
        return internalAuthorities.remove(authority);
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return hashedPW;
    }

    @Override
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

    public boolean isAllowLogin() {
        return allowLogin;
    }

    public void setAllowLogin(boolean allowLogin) {
        this.allowLogin = allowLogin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Concern getActiveConcern() {
        return activeConcern;
    }

    public void setActiveConcern(Concern activeConcern) {
        this.activeConcern = activeConcern;
    }

    public String getHashedPW() {
        return hashedPW;
    }

    public void setHashedPW(String hashedPW) {
        this.hashedPW = hashedPW;
    }
}
