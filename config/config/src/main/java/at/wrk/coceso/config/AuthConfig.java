package at.wrk.coceso.config;

import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthConfig {

  private final URL authUrl;

  private final boolean useAuthUrl;

  private final boolean firstUse;

  public static final int SUCCESS_CODE = 302;

  public static final int FAILURE_CODE = 401;

  @Autowired
  public AuthConfig(@Value("${auth.authUrl}") String authUrl, @Value("${auth.useAuthUrl}") Boolean useAuthUrl,
      @Value("${auth.firstUse}") Boolean firstUse) throws MalformedURLException {
    this.authUrl = new URL(authUrl);
    this.useAuthUrl = useAuthUrl == null ? false : useAuthUrl;
    this.firstUse = firstUse == null ? false : firstUse;
  }

  public URL getAuthUrl() {
    return authUrl;
  }

  public boolean useAuthUrl() {
    return useAuthUrl;
  }

  public boolean isFirstUse() {
    return firstUse;
  }

}
