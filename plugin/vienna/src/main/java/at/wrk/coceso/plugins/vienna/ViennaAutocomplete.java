package at.wrk.coceso.plugins.vienna;

import at.wrk.coceso.plugins.vienna.util.ViennaStreetParser;
import at.wrk.geocode.autocomplete.CsvAutocomplete;
import at.wrk.geocode.autocomplete.StreetnameAutocompleteSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

@Component
@Order(30)
public class ViennaAutocomplete extends CsvAutocomplete<String> implements StreetnameAutocompleteSupplier<String> {

    private final StreetnameResourceProvider resourceProvider;
    private final ViennaStreetParser streetParser;

    @Autowired
    public ViennaAutocomplete(final StreetnameResourceProvider resourceProvider, final ViennaStreetParser streetParser) {
        this.resourceProvider = resourceProvider;
        this.streetParser = streetParser;
    }

    @PostConstruct
    public void onInit() {
        this.initData();
    }

    @Override
    protected Map<String, String> loadData() {
        return loadData(StandardCharsets.UTF_8, ',', resourceProvider.get(), streetParser, Function.identity());
    }

    @Override
    public String getString(String value) {
        return value;
    }
}
