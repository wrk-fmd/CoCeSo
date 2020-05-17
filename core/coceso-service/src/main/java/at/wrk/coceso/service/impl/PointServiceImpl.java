package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.point.PointDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.entity.point.TextPoint;
import at.wrk.coceso.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
class PointServiceImpl implements PointService {

    private static final Logger LOG = LoggerFactory.getLogger(PointServiceImpl.class);

    private static final int MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH = 20;

    @Override
    public Point getPoint(Concern concern, PointDto data) {
        return data != null ? new TextPoint(data.getInfo()) : null;

//        String info = StringUtils.trimToNull(rawInfoString);
//
//        if (info == null) {
//            LOG.trace("Raw string '{}' resulted in null-string. Cannot be converted to valid point.", rawInfoString);
//            return null;
//        }
//
//        String[] parts = info.split("\n", 2);
//        Matcher matchedCoordinates = COORDINATE_PATTERN.matcher(parts[0].trim());
//        if (matchedCoordinates.find(0)) {
//            try {
//                double lat = Double.parseDouble(matchedCoordinates.group(1));
//                double lng = Double.parseDouble(matchedCoordinates.group(4));
//                return new CoordinatePoint(new LatLng(lat, lng));
//            } catch (NumberFormatException e) {
//                LOG.debug("Failed to parse coordinates from string which matched the coordinate pattern.", e);
//            }
//        }
//
//        if (unitSupplier != null && concern != null) {
//            String call = parts[0];
//            Unit group = unitSupplier.getTreatmentByCall(call, concern);
//            if (group != null) {
//                return new UnitPoint(group, parts.length >= 2 ? StringUtils.trimToNull(parts[1]) : null);
//            }
//        }
//
//        Poi poi = poiSupplier.getPoi(AutocompleteKeyParser.formatAutocompleteKey(info));
//        return poi == null ? AddressPointParser.parseFromString(info) : new PoiPoint(poi, info);
    }

    @Override
    public Collection<String> autocomplete(final String filter, final Concern concern) {
//        String normalizedLowercaseFilter = AutocompleteKeyParser.formatAutocompleteKey(filter);
//        if (normalizedLowercaseFilter == null || normalizedLowercaseFilter.length() <= 1) {
//            LOG.trace("Input for autocomplete is too short to search for results.");
//            return null;
//        }
//
//        Optional<String> intersectionPrefix = AutocompleteKeyParser.getPrefixForIntersectionSearch(filter);
//
//        Collection<String> filtered;
//        if (Concern.isClosedOrNull(concern)) {
//            filtered = autocomplete.getStartStringCollection(normalizedLowercaseFilter);
//            filtered.addAll(autocomplete.getContainingStringCollection(normalizedLowercaseFilter, MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH - filtered.size()));
//        } else if (intersectionPrefix.isPresent()) {
//            String actualQuery = getQueryForIntersectionSearch(filter, intersectionPrefix.get());
//
//            List<String> rawResults = streetnameAutocomplete.getStartString(actualQuery).collect(Collectors.toList());
//            if (rawResults.size() < MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH) {
//                rawResults.addAll(streetnameAutocomplete.getContainingStringCollection(actualQuery, null));
//            }
//
//            filtered = rawResults.stream()
//                    .map(result -> intersectionPrefix.get() + result)
//                    .collect(Collectors.toList());
//        } else {
//            filtered = searchForCallSignsAndPois(concern, normalizedLowercaseFilter);
//        }
//
//        return filtered.stream().limit(MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH).collect(Collectors.toList());
        return null;
    }

//    private String getQueryForIntersectionSearch(final String filter, final String intersectionPrefix) {
//        String firstLine = filter.split("[\r\n]")[0];
//        String firstLineWithoutPrefix = firstLine.substring(intersectionPrefix.length());
//        return firstLineWithoutPrefix.toLowerCase(Locale.ROOT);
//    }
//
//    private Collection<String> searchForCallSignsAndPois(final Concern concern, final String normalizedLowercaseFilter) {
//        Collection<String> filtered;
//        List<Unit> treatmentGroups = patadminService.getGroups(concern);
//        List<String> treatmentGroupsStartingWithQuery = getCallSignsStartingWithQuery(normalizedLowercaseFilter, treatmentGroups);
//        filtered = Stream.concat(
//                treatmentGroupsStartingWithQuery.stream(),
//                autocomplete.getStartString(normalizedLowercaseFilter)
//        ).collect(Collectors.toList());
//
//        if (filtered.size() < MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH) {
//            addResultsByContainsSearch(normalizedLowercaseFilter, treatmentGroups, filtered);
//        }
//        return filtered;
//    }
//
//    private void addResultsByContainsSearch(final String normalizedLowercaseFilter, final List<Unit> treatmentGroups, final Collection<String> filtered) {
//        List<String> treatmentGroupsContainingQuery = getCallSignsContainingQuery(normalizedLowercaseFilter, treatmentGroups);
//        List<String> resultsToAdd = Stream
//                .concat(
//                        treatmentGroupsContainingQuery.stream(),
//                        autocomplete.getContainingString(normalizedLowercaseFilter, null))
//                .limit(MAXIMUM_AUTOCOMPLETE_RESULT_LENGTH - filtered.size())
//                .collect(Collectors.toList());
//        filtered.addAll(resultsToAdd);
//    }
//
//    private List<String> getCallSignsContainingQuery(final String normalizedLowercaseFilter, final List<Unit> treatmentGroups) {
//        return treatmentGroups.stream()
//                .map(Unit::getCall)
//                .filter(u -> containsButDoesNotStartWith(u, normalizedLowercaseFilter))
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//    }
//
//    private static List<String> getCallSignsStartingWithQuery(final String normalizedLowercaseFilter, final List<Unit> treatmentGroups) {
//        return treatmentGroups
//                .stream()
//                .map(Unit::getCall)
//                .filter(u -> u.toLowerCase().startsWith(normalizedLowercaseFilter))
//                .distinct()
//                .sorted()
//                .collect(Collectors.toList());
//    }

    private static boolean containsButDoesNotStartWith(final String inputToCheck, final String searchString) {
        return inputToCheck.toLowerCase().indexOf(searchString) > 0;
    }
}
