package at.wrk.coceso.shell;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.support.util.FileUtils;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CocesoBannerProvider implements BannerProvider {

  @Override
  public String getBanner() {
    return FileUtils.readBanner(CocesoBannerProvider.class, "banner.txt") + OsUtils.LINE_SEPARATOR;
  }

  @Override
  public String getVersion() {
    return "2.0.0-SNAPSHOT";
  }

  @Override
  public String getWelcomeMessage() {
    return "Welcome to CoCeSo Shell. For most commands you have to choose a concern first." + OsUtils.LINE_SEPARATOR
        + "Type \"concern list\" to list available open concerns, \"concern select\" to select by ID." + OsUtils.LINE_SEPARATOR
        + "Type \"help\" to see all available commands.";
  }

  @Override
  public String getProviderName() {
    return "CoCeSo Shell";
  }

}
