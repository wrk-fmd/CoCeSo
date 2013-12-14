package at.wrk.coceso.utils;


import at.wrk.coceso.dao.OperatorDao;
import at.wrk.coceso.entity.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collection;

@Service
public class CocesoAuthenticationProvider implements AuthenticationProvider {
    // ###BEGIN### Third Party Authentication Config ###
    private final int SUCCESS = 302;
    private final int NOT_AUTHORIZED = 401;
    private final int ERROR = -1;
    // ###END### Third Party Authentication Config ###

    @Autowired
    private OperatorDao operatorDao;


    private String thirdPartyAuthenticationURL;

    @Autowired
    public CocesoAuthenticationProvider (String thirdPartyAuthenticationURL) {
        this.thirdPartyAuthenticationURL = thirdPartyAuthenticationURL;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        Operator user = operatorDao.getByUsername(username);

        if(user == null) {
            Logger.debug("User "+username+" not found");
            throw new BadCredentialsException("Wrong Username/Password");
        }
        if(!user.isEnabled()) {
            Logger.debug("User "+username+" has no Access Rights");
            throw new BadCredentialsException("Access Denied");
        }

        Collection<? extends GrantedAuthority> auth = user.getAuthorities();

        int returnCode;

        try { // #################### THIRD PARTY AUTHENTICATION ####################
            URL url = new URL(thirdPartyAuthenticationURL);
            String phrase = username+":"+password;
            String encoded = new String(Base64.encode(phrase.getBytes()));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty  ("Authorization", "Basic " + encoded);

            returnCode = connection.getResponseCode();

            connection.disconnect();

        }
        catch(MalformedURLException e){
            Logger.debug("Wrong URL! "+e.getMessage());
            returnCode = ERROR;
        }
        catch (ProtocolException e) {
            Logger.debug("ProtocolException: "+e.getMessage());
            returnCode = ERROR;
        }
        catch (IOException e) {
            Logger.debug("IOException (No Connection?) "+e.getMessage());
            returnCode = ERROR;
        }

        if(returnCode == NOT_AUTHORIZED) {
            Logger.debug("Online Authentication Failed");
            throw new BadCredentialsException("Wrong Username/Password");
        }
        if(returnCode == SUCCESS) { // Online Authentication succeeded
            if(!user.validatePassword(password)) { // Offline Auth failed -> set new Password in DB
                user.setPassword(password);
                operatorDao.update(user);
                Logger.debug("User "+username+": PW written to DB");
            }
            Logger.debug("User "+username+" authenticated online");
            return new UsernamePasswordAuthenticationToken(user,password, auth);
        }
        if(returnCode == ERROR) { // Online Auth not reachable
            if(!user.validatePassword(password)) { // Offline Auth failed
                Logger.debug("Offline Authentication failed");
                throw new BadCredentialsException("Wrong Username/Password");
            }
            Logger.debug("User "+username+" authenticated offline");
            return new UsernamePasswordAuthenticationToken(user, password, auth); // Offline Auth succeeded
        }
        else { // Wrong Status Code Definition?
            throw new BadCredentialsException("Internal Error");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
