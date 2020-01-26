package at.wrk.geocode.impl;

import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import at.wrk.geocode.autocomplete.StreetnameAutocompleteSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("ChainedStreetnameAutocomplete")
public class ChainedStreetnameAutocompleteSupplier extends ChainedAutocompleteSupplier {

    @Autowired(required = false)
    public ChainedStreetnameAutocompleteSupplier(final List<StreetnameAutocompleteSupplier<?>> autocomplete) {
        super(autocomplete.stream().map(supplier -> (AutocompleteSupplier<?>) supplier).collect(Collectors.toList()));
    }
}
