package at.wrk.coceso.entities;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Person implements UserDetails {
    public int id;

    public Case activeCase;

    public String given_name;
    public String sur_name;

    public int dNr;

    public String contact;

    @JsonIgnore
    public boolean allowLogin;

    @JsonIgnore
    public String hashedPW;

    @JsonIgnore
    public String username;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> ret = new ArrayList<GrantedAuthority>();

        if(allowLogin) ret.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "MLS";
            }
        });

        return ret;
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
