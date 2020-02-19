package at.wrk.coceso.plugin.geobroker.action.factory;

import at.wrk.coceso.plugin.geobroker.controller.OneTimeActionController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActionUrlFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ActionUrlFactory.class);

    private final String baseUrl;

    @Autowired
    public ActionUrlFactory(@Value("${geobroker.ota.base.url:}") final String baseUrl) {
        this.baseUrl = formatBaseUrl(baseUrl);
        logConfiguration();
    }

    public String generateUrl(final UUID actionId) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("No base URL configured to generate public URL of one-time-action!");
        }

        return baseUrl + OneTimeActionController.ENDPOINT + "/" + actionId.toString();
    }

    boolean isOneTimeActionFeatureActive() {
        return baseUrl != null;
    }

    private void logConfiguration() {
        if (baseUrl != null) {
            LOG.info("Base URL for One-Time-Actions is configured: '{}'", baseUrl);
        } else {
            LOG.info("No base URL for One-Time-Actions configured. Feature is disabled.");
        }
    }

    private static String formatBaseUrl(final String baseUrl) {
        String url = StringUtils.trimToNull(baseUrl);
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }
}
