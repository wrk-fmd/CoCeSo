package at.wrk.coceso.plugins.vcm;

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
@Order(11)
public class VcmJsonPoi extends JsonPoi {
    private static final Logger LOG = LoggerFactory.getLogger(VcmJsonPoi.class);

    @Autowired
    public VcmJsonPoi(final ObjectMapper mapper) {
        super(mapper, new ClassPathResource("vcm.json", VcmJsonPoi.class.getClassLoader()));
    }

    @PostConstruct
    public void onInit() {
        CompletableFuture
                .runAsync(this::loadData)
                .whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        LOG.warn("Failed to load autocomplete data from VCM JSON POIs file!", throwable);
                    }
                });
    }
}
