package at.wrk.geocode.poi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Base class for providing POI entries from JSON resources in the "old" Coceso specific format
 */
public abstract class JsonPoi extends AbstractJsonPoi {

    private static final Logger LOG = LoggerFactory.getLogger(JsonPoi.class);

    private final ObjectMapper objectMapper;
    private final List<Resource> resources;

    public JsonPoi(ObjectMapper objectMapper, String path) throws IOException {
        this(objectMapper, new PathMatchingResourcePatternResolver().getResources(path));
    }

    /**
     * Read POI entries from the given resources, parse them using the provided object mapper and store them in-memory.
     */
    public JsonPoi(ObjectMapper objectMapper, Resource... sources) {
        this.objectMapper = objectMapper;
        this.resources = Arrays.asList(sources);
    }

    @Override
    protected Stream<Poi> readResources() {
        return this.resources
            .stream()
            .flatMap(this::readResource);
    }

    private Stream<Poi> readResource(final Resource source) {
        try (InputStream inputStream = source.getInputStream()) {
            return Arrays.stream(objectMapper.readValue(inputStream, PoiImpl[].class));
        } catch (IOException ex) {
            LOG.error("Error reading POI data", ex);
            return Stream.empty();
        }
    }
}
