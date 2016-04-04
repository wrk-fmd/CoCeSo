package at.wrk.coceso.config;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("cocesoConfig")
public class CocesoConfig {

  private final boolean debug;

  private final Locale defaultLocale;

  private final String jsPlugins;

  private final String gmapsApiKey;

  @Autowired
  public CocesoConfig(@Value("${debug:false}") boolean debug, @Value("${locale.default:en}") String defaultLocale,
      @Value("${js.plugins:}") String jsPlugins, @Value("${gmaps.apikey:}") String gmapsApiKey) {
    this.debug = debug;
    this.defaultLocale = Locale.forLanguageTag(defaultLocale);
    this.jsPlugins = StringUtils.isBlank(jsPlugins) ? "{}" : jsPlugins;
    this.gmapsApiKey = StringUtils.trimToNull(gmapsApiKey);
  }

  public boolean isDebug() {
    return debug;
  }

  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  public String getJsPlugins() {
    return jsPlugins;
  }

  public String getGmapsApiKey() {
    return gmapsApiKey;
  }

}
