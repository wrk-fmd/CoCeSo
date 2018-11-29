package at.wrk.geocode.autocomplete;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

@FunctionalInterface
public interface CSVRecordParser<T> {
    List<T> parseCsvRecord(CSVRecord csvRecord);
}
