package at.wrk.geocode.impl;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.autocomplete.AutocompleteKeyParser;
import at.wrk.geocode.autocomplete.PreloadedAutocomplete;
import at.wrk.geocode.poi.Poi;
import at.wrk.geocode.poi.PoiSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for providing POI entries from GeoJson resources
 */
@Component
@Order(10)
public class GeoJsonPoi extends PreloadedAutocomplete<Poi> implements PoiSupplier {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ObjectMapper mapper;
    private final Path geojsonPath;

    @Autowired
    public GeoJsonPoi(ObjectMapper mapper, @Value("${geojson.path}") Path geojsonPath) {
        this.mapper = mapper;
        this.geojsonPath = geojsonPath;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void onInit() {
        this.initData();
    }

    @Override
    protected Map<String, Poi> loadData() {
        if (geojsonPath == null) {
            LOG.info("No GeoJSON path provided");
            return Collections.emptyMap();
        }

        Path configFile = geojsonPath.resolve("coceso-geojson.properties");
        if (!Files.exists(configFile)) {
            LOG.warn("GeoJSON config file not found: {}", configFile);
            return Collections.emptyMap();
        }

        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(configFile));
        } catch (IOException e) {
            LOG.error("Error reading GeoJSON config file {}", configFile, e);
            return Collections.emptyMap();
        }

        return properties.entrySet().stream()
            .flatMap(e -> loadFeatureCollection(e.getKey().toString(), e.getValue().toString()))
            .filter(poi -> poi.getText() != null)
            .collect(Collectors.toMap(
                poi -> AutocompleteKeyParser.formatAutocompleteKey(poi.getText()),
                Function.identity(),
                (a, b) -> a
            ));
    }

    private Stream<Poi> loadFeatureCollection(String file, String prefix) {
        Path filePath = geojsonPath.resolve("overlays").resolve(file + ".json");
        if (!Files.exists(filePath)) {
            LOG.warn("Ignoring non-existing GeoJSON file {}", filePath);
            return null;
        }

        LOG.debug("Reading POI features from {} under prefix '{}'", filePath, prefix);
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            String fullPrefix = StringUtils.isBlank(prefix) ? "" : prefix.trim() + "/";
            FeatureCollection collection = mapper.readValue(inputStream, FeatureCollection.class);
            LOG.debug("Read FeatureCollection with {} features", collection.getFeatures().size());
            return collection.getFeatures().stream()
                .map(f -> getPoi(f, fullPrefix))
                .filter(Objects::nonNull);
        } catch (IOException ex) {
            LOG.error("Error reading POI data", ex);
            return Stream.empty();
        }
    }

    private Poi getPoi(Feature feature, String prefix) {
        GeoJsonObject geometry = feature.getGeometry();
        if (!(geometry instanceof Point)) {
            return null;
        }

        LngLatAlt coordinates = ((Point) geometry).getCoordinates();
        if (coordinates == null) {
            return null;
        }

        String text = feature.getProperty("text");
        if (text == null) {
            return null;
        }

        return new PoiImpl(
            prefix + text,
            new LatLng(coordinates.getLatitude(), coordinates.getLongitude())
        );
    }


    @Override
    public String getString(Poi value) {
        return value.getText();
    }

    /**
     * Find the best matching POI for the given text
     *
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
