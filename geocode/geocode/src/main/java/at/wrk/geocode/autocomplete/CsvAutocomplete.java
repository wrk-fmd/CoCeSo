package at.wrk.geocode.autocomplete;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A pre-loaded autocomplete solution loading its data from CSV
 *
 * @param <T>
 */
public abstract class CsvAutocomplete<T> extends PreloadedAutocomplete<T> {

    private final static Logger LOG = LoggerFactory.getLogger(CsvAutocomplete.class);

    /**
     * Load entries from a resource containing CSV data
     *
     * @param charset              The charset used for parsing the resource
     * @param delimiter            The column delimiter
     * @param source               The CSV resource
     * @param parser               Function to parse each CSV record to entries
     * @param keyExtractorFunction Function to get the String used as key from each entry
     */
    protected final void loadData(
            final Charset charset,
            final char delimiter,
            final Resource source,
            final CSVRecordParser<T> parser,
            final Function<T, String> keyExtractorFunction) {
        String inputResourceAsString = readInputAsString(charset, source);

        CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(delimiter).withHeader();
        try (CSVParser csvParser = CSVParser.parse(inputResourceAsString, csvFormat)) {
            StopWatch stopWatch = StopWatch.createStarted();
            Map<String, T> loadedCsvData = getEntitiesFromCsv(parser, keyExtractorFunction, csvParser);
            super.values.putAll(loadedCsvData);
            stopWatch.stop();
            Duration parseDuration = Duration.ofNanos(stopWatch.getNanoTime());
            LOG.info(
                    "Successfully loaded {} entries of CSV file for autocomplete of feature {}. Parsing took {}.",
                    loadedCsvData.size(),
                    this.getClass().getSimpleName(),
                    parseDuration);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Map<String, T> getEntitiesFromCsv(
            final CSVRecordParser<T> parser,
            final Function<T, String> keyExtractorFunction,
            final CSVParser csvParser) {
        return StreamSupport
                .stream(csvParser.spliterator(), true)
                .map(parser::parseCsvRecord)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        keyExtractorFunction.andThen(AutocompleteKeyParser::formatAutocompleteKey),
                        Function.identity(),
                        (a, b) -> a, TreeMap::new));
    }

    private String readInputAsString(final Charset charset, final Resource source) {
        String inputResourceAsString;
        try (InputStream inputStream = source.getInputStream()) {
            inputResourceAsString = StreamUtils.copyToString(inputStream, charset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return inputResourceAsString;
    }
}
