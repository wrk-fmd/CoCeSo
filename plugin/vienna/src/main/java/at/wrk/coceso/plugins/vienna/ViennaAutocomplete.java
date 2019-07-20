package at.wrk.coceso.plugins.vienna;

import at.wrk.coceso.plugins.vienna.util.ViennaStreetParser;
import at.wrk.geocode.autocomplete.CsvAutocomplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
@Order(30)
public class ViennaAutocomplete extends CsvAutocomplete<String> {
    private static final Logger LOG = LoggerFactory.getLogger(ViennaAutocomplete.class);

    private final StreetnameResourceProvider resourceProvider;
    private final ViennaStreetParser streetParser;
    private boolean initialized;

    @Autowired
    public ViennaAutocomplete(final StreetnameResourceProvider resourceProvider, final ViennaStreetParser streetParser) {
        this.resourceProvider = resourceProvider;
        this.streetParser = streetParser;
    }

    @PostConstruct
    public void onInit() {
        CompletableFuture
                .runAsync(() -> loadData(StandardCharsets.ISO_8859_1, ',', resourceProvider.get(), streetParser, Function.identity()))
                .whenComplete((ignored, throwable) -> {
                    initialized = true;
                    if (throwable != null) {
                        LOG.warn("Failed to load autocomplete data for Vienna streetnames from CSV file!", throwable);
                    }
                });
    }

    boolean isInitialized() {
        return initialized;
    }

    @Override
    public String getString(String value) {
        return value;
    }
}
