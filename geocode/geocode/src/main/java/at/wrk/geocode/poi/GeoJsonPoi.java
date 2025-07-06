package at.wrk.geocode.poi;

import at.wrk.geocode.LatLng;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for providing POI entries from GeoJson resources
 */
public abstract class GeoJsonPoi extends AbstractJsonPoi {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ObjectMapper mapper;
    private final List<PoiResource> resources;

    public GeoJsonPoi(ObjectMapper mapper, String path, String prefix) throws IOException {
        this.mapper = mapper;
        this.resources = Arrays.stream(new PathMatchingResourcePatternResolver().getResources(path))
            .map(resource -> new PoiResource(resource, prefix))
            .collect(Collectors.toList());
    }

    /**
     * Read POI entries from the given resources, parse them using the provided object mapper and store them in-memory.
     */
    public GeoJsonPoi(ObjectMapper mapper, List<PoiResource> resources) {
        this.mapper = mapper;
        this.resources = resources;
    }

    @Override
    protected Stream<Poi> readResources() {
        return this.resources
            .stream()
            .flatMap(source -> loadFeatureCollection(source.resource, source.prefix));
    }

    private Stream<Poi> loadFeatureCollection(Resource source, String prefix) {
        LOG.debug("Reading POI features from {}", source);

        try (InputStream inputStream = source.getInputStream()) {
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

    public static final class PoiResource {

        private final Resource resource;
        private final String prefix;

        public PoiResource(Resource resource) {
            this(resource, null);
        }

        public PoiResource(Resource resource, String prefix) {
            this.resource = resource;
            this.prefix = prefix != null ? prefix : extractPrefix(resource);
        }

        private static String extractPrefix(Resource resource) {
            try {
                String prefix = resource.getURI().toString();

                int geojsonIndex = prefix.lastIndexOf("/geojson/");
                if (geojsonIndex < 0) {
                    // Automatic prefix detection requires relative path to "geojson" directory
                    return null;
                }

                prefix = prefix.substring(geojsonIndex + 9);
                int separatorIndex = prefix.lastIndexOf("/");
                if (separatorIndex < 0) {
                    // Only file name, no prefix
                    return null;
                }

                // Return the path without the filename part
                return prefix.substring(0, separatorIndex);
            } catch (IOException e) {
                return null;
            }
        }
    }
}
