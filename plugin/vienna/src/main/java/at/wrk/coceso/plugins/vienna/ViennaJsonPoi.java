package at.wrk.coceso.plugins.vienna;

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
@Order(10)
public class ViennaJsonPoi extends GeoJsonPoi {

    private static final Logger LOG = LoggerFactory.getLogger(ViennaJsonPoi.class);

    @Autowired
    public ViennaJsonPoi(ObjectMapper mapper) throws IOException {
        super(mapper, "classpath*:/geojson/**/*.json", null);
    }

    @PostConstruct
    public void onInit() {
        CompletableFuture
                .runAsync(this::loadData)
                .whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        LOG.warn("Failed to load autocomplete data from Vienna JSON POIs file!", throwable);
                    }
                });
    }
}
