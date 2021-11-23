package at.wrk.coceso.parser.staff;

import java.util.Collection;

/**
 * This can be implemented and exposed as a bean by plugins to allow parsing staff members from a specific CSV format
 */
public interface StaffParser {

    /**
     * Parse the imported staff members
     *
     * @param data The CSV data
     * @return A collection of parsed staff members
     */
    Collection<ParsedStaffMember> parse(String data) throws CsvParsingException;
}
