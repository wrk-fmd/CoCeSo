package at.wrk.coceso.utils;


import at.wrk.coceso.dao.PersonDao;
import at.wrk.coceso.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CocesoAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PersonDao personDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        Person user = personDao.getByUsername(username);

        if(user == null) throw new BadCredentialsException("User not found");
        if(!user.isEnabled()) throw new BadCredentialsException("Access Denied");

        // TODO Implement NIU Authentication

        if(!user.validatePassword(password)) throw new BadCredentialsException("Wrong Password");

        Collection<? extends GrantedAuthority> auth = user.getAuthorities();

        return new UsernamePasswordAuthenticationToken(username,password, auth);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
