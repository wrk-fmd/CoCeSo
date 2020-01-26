package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.geocode.autocomplete.AutocompleteKeyParser;
import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
class PointServiceImpl implements PointService {
    private static final Logger LOG = LoggerFactory.getLogger(PointServiceImpl.class);

    private static final int MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH = 20;

    @Autowired
    private PatadminService patadminService;

    private final AutocompleteSupplier<?> autocomplete;
    private final AutocompleteSupplier<?> streetnameAutocomplete;

    @Autowired
    public PointServiceImpl(
            final @Qualifier("ChainedAutocomplete") AutocompleteSupplier<?> autocomplete,
            final @Qualifier("ChainedStreetnameAutocomplete") AutocompleteSupplier<?> streetnameAutocomplete) {
        this.autocomplete = autocomplete;
        this.streetnameAutocomplete = streetnameAutocomplete;
    }

    @Override
    public Collection<String> autocomplete(final String filter, final Concern concern) {
        String normalizedLowercaseFilter = AutocompleteKeyParser.formatAutocompleteKey(filter);
        if (normalizedLowercaseFilter == null || normalizedLowercaseFilter.length() <= 1) {
            LOG.trace("Input for autocomplete is too short to search for results.");
            return null;
        }

        Optional<String> intersectionPrefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(filter);

        Collection<String> filtered;
        if (Concern.isClosedOrNull(concern)) {
            filtered = autocomplete.getStartStringCollection(normalizedLowercaseFilter);
            filtered.addAll(autocomplete.getContainingStringCollection(normalizedLowercaseFilter, MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH - filtered.size()));
        } else if (intersectionPrefix.isPresent()) {
            String actualQuery = getQueryForIntersectionSearch(filter, intersectionPrefix.get());

            List<String> rawResults = streetnameAutocomplete.getStartString(actualQuery).collect(Collectors.toList());
            if (rawResults.size() < MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH) {
                rawResults.addAll(streetnameAutocomplete.getContainingStringCollection(actualQuery, null));
            }

            filtered = rawResults.stream()
                    .map(result -> intersectionPrefix.get() + result)
                    .collect(Collectors.toList());
        } else {
            filtered = searchForCallSignsAndPois(concern, normalizedLowercaseFilter);
        }

        return filtered.stream().limit(MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH).collect(Collectors.toList());
    }

    private String getQueryForIntersectionSearch(final String filter, final String intersectionPrefix) {
        String firstLine = filter.split("[\r\n]")[0];
        String firstLineWithoutPrefix = firstLine.substring(intersectionPrefix.length());
        return firstLineWithoutPrefix.toLowerCase(Locale.ROOT);
    }

    private Collection<String> searchForCallSignsAndPois(final Concern concern, final String normalizedLowercaseFilter) {
        Collection<String> filtered;
        List<Unit> treatmentGroups = patadminService.getGroups(concern);
        List<String> treatmentGroupsStartingWithQuery = getCallSignsStartingWithQuery(normalizedLowercaseFilter, treatmentGroups);
        filtered = Stream.concat(
                treatmentGroupsStartingWithQuery.stream(),
                autocomplete.getStartString(normalizedLowercaseFilter)
        ).collect(Collectors.toList());

        if (filtered.size() < MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH) {
            addResultsByContainsSearch(normalizedLowercaseFilter, treatmentGroups, filtered);
        }
        return filtered;
    }

    private void addResultsByContainsSearch(final String normalizedLowercaseFilter, final List<Unit> treatmentGroups, final Collection<String> filtered) {
        List<String> treatmentGroupsContainingQuery = getCallSignsContainingQuery(normalizedLowercaseFilter, treatmentGroups);
        List<String> resultsToAdd = Stream
                .concat(
                        treatmentGroupsContainingQuery.stream(),
                        autocomplete.getContainingString(normalizedLowercaseFilter, null))
                .limit(MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH - filtered.size())
                .collect(Collectors.toList());
        filtered.addAll(resultsToAdd);
    }

    private List<String> getCallSignsContainingQuery(final String normalizedLowercaseFilter, final List<Unit> treatmentGroups) {
        return treatmentGroups.stream()
                .map(Unit::getCall)
                .filter(u -> containsButDoesNotStartWith(u, normalizedLowercaseFilter))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private static List<String> getCallSignsStartingWithQuery(final String normalizedLowercaseFilter, final List<Unit> treatmentGroups) {
        return treatmentGroups
                .stream()
                .map(Unit::getCall)
                .filter(u -> u.toLowerCase().startsWith(normalizedLowercaseFilter))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private static boolean containsButDoesNotStartWith(final String inputToCheck, final String searchString) {
        return inputToCheck.toLowerCase().indexOf(searchString) > 0;
    }
}
