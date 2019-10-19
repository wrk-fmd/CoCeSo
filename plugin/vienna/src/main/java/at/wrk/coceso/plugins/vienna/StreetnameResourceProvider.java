package at.wrk.coceso.plugins.vienna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Component
public class StreetnameResourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(StreetnameResourceProvider.class);

    private final static String URL_CACHE = "streetnames.csv";
    private final static String URL_ORIGINAL = "http://data.wien.gv.at/daten/geo?service=WFS&version=1.1.0&request=GetFeature" +
            "&typeName=ogdwien:GEONAMENSVERZOGD&propertyName=STR_NAME,BEZLISTE&outputFormat=csv";

    private final boolean useCache;

    public StreetnameResourceProvider() {
        this(true);
    }

    public StreetnameResourceProvider(final boolean useCache) {
        this.useCache = useCache;
    }

    public Resource get() {
        try {
            return useCache ? new ClassPathResource(URL_CACHE, getClass().getClassLoader()) : new UrlResource(URL_ORIGINAL);
        } catch (MalformedURLException e) {
            LOG.error("Failed to create URL from hardcoded URL string.", e);
            throw new RuntimeException(e);
        }
    }
}
