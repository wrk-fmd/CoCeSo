package at.wrk.coceso.plugins.vienna;

import at.wrk.coceso.plugins.vienna.util.ViennaStreetParser;
import at.wrk.geocode.autocomplete.CsvAutocomplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;

@Component
@Order(30)
public class ViennaAutocomplete extends CsvAutocomplete<String> {

    @Autowired
    public ViennaAutocomplete(final StreetnameResourceProvider resourceProvider, final ViennaStreetParser streetParser) throws IOException {
        super(Charset.forName("ISO-8859-1"), ',', resourceProvider.get(), streetParser, Function.identity());
    }

    @Override
    public String getString(String value) {
        return value;
    }
}
