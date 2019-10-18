package at.wrk.coceso.importer;

import at.wrk.coceso.entity.User;

import java.util.Collection;

public interface UserImporter {

    /**
     * Parses the serialized data and returns all created or updated users.
     * @param csvData the serialized input data (CSV, JSON, XML, ...).
     * @param existing the existing users configured in the system.
     * @return Created or updated users.
     * @throws ImportException is thrown if the importer does not support the input data, or the input data is malformed.
     */
    Collection<User> updateUsers(String csvData, Collection<User> existing) throws ImportException;
}
