package at.wrk.geocode.poi;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import at.wrk.geocode.autocomplete.PreloadedAutocomplete;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for providing POI entries from JSON resources
 */
public abstract class JsonPoi extends PreloadedAutocomplete<Poi> implements PoiSupplier {

    private static final Logger LOG = LoggerFactory.getLogger(JsonPoi.class);

    private final ObjectMapper objectMapper;
    private final List<Resource> resources;

    public JsonPoi(ObjectMapper objectMapper, String path) throws IOException {
        this(objectMapper, new PathMatchingResourcePatternResolver().getResources(path));
    }

    /**
     * Read POI entries from the given resources, parse them using the provided object mapper and store them in-memory.
     *
     * @param objectMapper
     * @param sources
     */
    public JsonPoi(ObjectMapper objectMapper, Resource... sources) {
        this.objectMapper = objectMapper;
        this.resources = Arrays.asList(sources);
    }


    protected final void loadData() {
        StopWatch stopWatch = StopWatch.createStarted();
        Map<String, Poi> loadedData = mapDataToPoiMap();
        super.values.putAll(loadedData);
        stopWatch.stop();
        Duration parseDuration = Duration.ofNanos(stopWatch.getNanoTime());
        LOG.info(
                "Successfully loaded {} entries of JSON file for autocomplete of feature {}. Parsing took {}.",
                loadedData.size(),
                this.getClass().getSimpleName(),
                parseDuration);
    }

    private Map<String, Poi> mapDataToPoiMap() {
        return this.resources
                .stream()
                .flatMap(this::readResource)
                .filter(poi -> poi.getText() != null)
                .collect(Collectors.toMap(
                        poi -> AutocompleteSupplier.getKey(poi.getText()),
                        Function.identity(),
                        (a, b) -> a, TreeMap::new));
    }

    private Stream<Poi> readResource(final Resource source) {
        try (InputStream inputStream = source.getInputStream()) {
            return Arrays.stream(objectMapper.readValue(inputStream, PoiImpl[].class));
        } catch (IOException ex) {
            LOG.error("Error reading POI data", ex);
            return Stream.empty();
        }
    }

    @Override
    public String getString(Poi value) {
        return value.getText();
    }

    /**
     * Find the best matching POI for the given text
     *
     * @param text
     * @return An entry such that text starts with that entries description
     */
    @Override
    public Poi getPoi(String text) {
        if (text == null) {
            return null;
        }

        Map.Entry<String, Poi> entry = values.floorEntry(text);
        return (entry != null && text.startsWith(entry.getKey())) ? entry.getValue() : null;
    }

    /**
     * Geocode a POI to its coordinates, if it is present
     *
     * @param poi
     * @return
     */
    @Override
    public LatLng geocode(Poi poi) {
        if (StringUtils.isBlank(poi.getText())) {
            return null;
        }

        Poi found = values.get(poi.getText().trim().toLowerCase());
        return found == null ? null : found.getCoordinates();
    }

    /**
     * Find the POI closest to the specified coordinates
     *
     * @param coordinates
     * @return
     */
    @Override
    public ReverseResult<Poi> reverse(LatLng coordinates) {
        // Only look for POI within 100 meters
        Bounds bounds = coordinates.boundsForDistance(100);
        ReverseResult<Poi> nearest = null;
        for (Poi poi : values.values()) {
            if (bounds.contains(poi.getCoordinates())) {
                int dist = coordinates.distance(poi.getCoordinates());
                if (nearest == null || dist < nearest.dist) {
                    nearest = new ReverseResult<>(dist, poi, poi.getCoordinates());
                    if (dist == 0) {
                        // Found an exact match
                        break;
                    }
                }
            }
        }

        return nearest;
    }

}
