package at.wrk.coceso.plugins.wflwr;

import at.wrk.geocode.poi.GeoJsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Order(12)
public class WflwrJsonPoi extends GeoJsonPoi {

    private static final Logger LOG = LoggerFactory.getLogger(WflwrJsonPoi.class);

    @Autowired
    public WflwrJsonPoi(ObjectMapper mapper) throws IOException {
        super(mapper, "classpath*:/wflwr*.json", "WFLWR");
    }

    @PostConstruct
    public void onInit() {
        CompletableFuture
            .runAsync(this::loadData)
            .whenComplete((ignored, throwable) -> {
                if (throwable != null) {
                    LOG.warn("Failed to load autocomplete data from WFLWR JSON POIs file!", throwable);
                }
            });
    }
}
