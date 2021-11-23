package at.wrk.coceso.parser;

/**
 * This should be implemented by plugins and returned by a provider bean
 */
public interface ImportParser<T> {

    /**
     * Parses a single row from the CSV file
     *
     * @param row The input CSV row
     * @return The parsed object
     */
    T parseRow(String row);
}
