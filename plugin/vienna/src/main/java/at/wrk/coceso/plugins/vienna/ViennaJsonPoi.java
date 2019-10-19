package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.poi.JsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Component
@Order(10)
public class ViennaJsonPoi extends JsonPoi {
    private static final Logger LOG = LoggerFactory.getLogger(ViennaJsonPoi.class);

    @Autowired
    public ViennaJsonPoi(final ObjectMapper mapper) {
        super(
                mapper,
                new ClassPathResource("ehs.json", ViennaJsonPoi.class.getClassLoader()),
                new ClassPathResource("hospitals.json", ViennaJsonPoi.class.getClassLoader()),
                new ClassPathResource("wrk.json", ViennaJsonPoi.class.getClassLoader()));
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
