package at.wrk.geocode.autocomplete;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Abstract base class for autocomplete implementations with (sorted) in-memory storage
 *
 * @param <T> The type of data stored
 */
public abstract class PreloadedAutocomplete<T> implements AutocompleteSupplier<T> {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * The data store for all available entries
   */
  protected final NavigableMap<String, T> values;

  private boolean initialized;

  protected PreloadedAutocomplete() {
    this.values = new TreeMap<>();
  }

  boolean isInitialized() {
    return initialized;
  }

  protected final void initData() {
    CompletableFuture
        .runAsync(this::doInitData)
        .whenComplete((ignored, throwable) -> {
          initialized = true;
          if (throwable != null) {
            LOG.warn("Failed to load autocomplete data for class {}", getClass().getSimpleName(), throwable);
          }
        });
    }

  private void doInitData() {
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, T> loadedData = loadData();
    values.clear();
    values.putAll(loadedData);
    stopWatch.stop();
    Duration parseDuration = Duration.ofNanos(stopWatch.getNanoTime());
    LOG.info(
        "Successfully loaded {} entries for autocomplete {}. Parsing took {}.",
        loadedData.size(),
        this.getClass().getSimpleName(),
        parseDuration
    );
  }

  protected abstract Map<String, T> loadData();

  @Override
  public Stream<T> getStart(final String filter) {
    String to = filter.substring(0, filter.length() - 1) + (char) (filter.charAt(filter.length() - 1) + 1);
    return values.subMap(filter, to).values().stream();
  }

  @Override
  public Stream<T> getContaining(final String filter, final Integer max) {
    if (max != null && max <= 0) {
      return Stream.empty();
    }

    Stream<T> filtered = values.entrySet()
            .stream()
            .filter(e -> e.getKey().indexOf(filter) > 0)
            .map(Map.Entry::getValue);
    return max == null ? filtered : filtered.limit(max);
  }

}
