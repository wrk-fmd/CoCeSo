package at.wrk.geocode.autocomplete;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A preloaded autocomplete solution loading its data from CSV
 *
 * @param <T>
 */
public abstract class CsvAutocomplete<T> extends PreloadedAutocomplete<T> {

    /**
     * Load entries from a resource containing CSV data
     *
     * @param charset              The charset used for parsing the resource
     * @param delimiter            The column delimiter
     * @param source               The CSV resource
     * @param parser               Function to parse each CSV record to entries
     * @param keyExtractorFunction Function to get the String used as key from each entry
     */
    protected final Map<String, T> loadData(
            final Charset charset,
            final char delimiter,
            final Resource source,
            final CSVRecordParser<T> parser,
            final Function<T, String> keyExtractorFunction) {
        String inputResourceAsString = readInputAsString(charset, source);

        CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(delimiter).withHeader();
        try (CSVParser csvParser = CSVParser.parse(inputResourceAsString, csvFormat)) {
            return getEntitiesFromCsv(parser, keyExtractorFunction, csvParser);
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
