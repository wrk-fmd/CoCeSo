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
import org.apache.log4j.Logger;

@Service
public class CocesoAuthenticationProvider implements AuthenticationProvider {

    private Logger logger = Logger.getLogger("CoCeSo");

    private final boolean firstUse;

    private final boolean useThirdPartyAuth;

    // ###BEGIN### Third Party Authentication Config ###
    private static final int SUCCESS = 302;
    private static final int NOT_AUTHORIZED = 401;
    private static final int ERROR = -1;
    // ###END### Third Party Authentication Config ###

    @Autowired
    private OperatorDao operatorDao;


    private String thirdPartyAuthenticationURL;

    @Autowired
    public CocesoAuthenticationProvider (String thirdPartyAuthenticationURL, Boolean useThirdPartyAuth, Boolean firstUse)
    {
        this.thirdPartyAuthenticationURL = thirdPartyAuthenticationURL;
        this.useThirdPartyAuth = useThirdPartyAuth == null ? false : useThirdPartyAuth;
        this.firstUse = firstUse == null ? false : firstUse;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        Operator user = operatorDao.getByUsername(username);

        if(user == null) {
            logger.info("[failed] " + username + ": User not found");
            throw new BadCredentialsException("Wrong Username/Password");
        }
        if(!user.isEnabled()) {
            logger.info("[failed] " + username + ": Login deactivated");
            throw new BadCredentialsException("Access Denied");
        }

        Collection<? extends GrantedAuthority> auth = user.getAuthorities();

        int returnCode;

        boolean offline_failed = !user.validatePassword(password);

        logger.info("[" + (offline_failed ? "failed" : "  OK  ") + "] " + username + ": Offline Authentication ");

        if(offline_failed && useThirdPartyAuth) {
            logger.info(username + ": Using Online Authentication");
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
                logger.info("Wrong URL! "+e.getMessage());
                returnCode = ERROR;
            }
            catch (ProtocolException e) {
                logger.info("ProtocolException: "+e.getMessage());
                returnCode = ERROR;
            }
            catch (IOException e) {
                logger.info("IOException (No Connection?) "+e.getMessage());
                returnCode = ERROR;
            }


        } else if(offline_failed && !useThirdPartyAuth) {
            returnCode = firstUse ? SUCCESS : NOT_AUTHORIZED;
        } else {
            returnCode = SUCCESS;
        }

        if(returnCode == NOT_AUTHORIZED) {
            logger.info("[failed] " + username + ": Authentication");
            throw new BadCredentialsException("Wrong Username/Password");
        }
        if(returnCode == SUCCESS) {
            if(offline_failed) {
                user.setPassword(password);
                operatorDao.update(user);
                logger.info("User "+username+": PW written to DB");
            }
            logger.info("[  OK  ] " + username + ": Authentication");
            return new UsernamePasswordAuthenticationToken(user,password, auth);
        }
        if(returnCode == ERROR) {

            logger.info("ERROR - Authentication failed");
            throw new BadCredentialsException("Wrong Username/Password");
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
