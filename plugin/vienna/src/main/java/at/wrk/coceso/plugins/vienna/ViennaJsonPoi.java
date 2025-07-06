package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.poi.GeoJsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class ViennaJsonPoi extends GeoJsonPoi {

    private static final Logger LOG = LoggerFactory.getLogger(ViennaJsonPoi.class);

    @Autowired
    public ViennaJsonPoi(ObjectMapper mapper, @Value("${geojson.path}") Path geojsonPath) {
        super(mapper, getResources(geojsonPath));
    }

    private static List<PoiResource> getResources(Path geojsonPath) {
        if (geojsonPath == null) {
            LOG.info("No GeoJSON path provided");
            return Collections.emptyList();
        }

        Path configFile = geojsonPath.resolve("coceso-geojson.properties");
        if (!Files.exists(configFile)) {
            LOG.warn("GeoJSON config file not found: {}", configFile);
            return Collections.emptyList();
        }

        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(configFile));
        } catch (IOException e) {
            LOG.error("Error reading GeoJSON config file {}", configFile, e);
            return Collections.emptyList();
        }

        Path dataPath = geojsonPath.resolve("overlays");
        return properties.entrySet().stream()
                .map(e -> getResource(dataPath, e.getKey().toString(), e.getValue().toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static PoiResource getResource(Path dataPath, String file, String prefix) {
        Path filePath = dataPath.resolve(file + ".json");
        if (!Files.exists(filePath)) {
            LOG.warn("Ignoring non-existing GeoJSON file {}", filePath);
            return null;
        }

        LOG.info("Registering GeoJSON file {} under prefix '{}'", filePath, prefix);
        Resource resource = new FileSystemResourceLoader().getResource("file:" + filePath);
        return new PoiResource(resource, prefix);
    }

    @PostConstruct
    public void onInit() {
        CompletableFuture
                .runAsync(this::loadData)
                .whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        LOG.warn("Failed to load autocomplete data from Vienna JSON POIs file!", throwable);
                    }
                });
    }
}
