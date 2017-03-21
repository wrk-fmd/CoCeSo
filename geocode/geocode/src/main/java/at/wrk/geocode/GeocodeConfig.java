package at.wrk.geocode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeocodeConfig {

  @Value("${geocode.db.driver}")
  private String driver;

  @Value("${geocode.db.url}")
  private String url;

  @Value("${geocode.db.username}")
  private String username;

  @Value("${geocode.db.password}")
  private String password;

  private final String gmapsApiKey;

  @Autowired
  public GeocodeConfig(@Value("${geocode.gmaps.apikey:}") String gmapsApiKey) {
    this.gmapsApiKey = StringUtils.trimToNull(gmapsApiKey);
  }

  public String getDriver() {
    return driver;
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getGmapsApiKey() {
    return gmapsApiKey;
  }

}
