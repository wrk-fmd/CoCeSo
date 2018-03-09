package at.wrk.coceso.niu.parser;

import at.wrk.coceso.importer.ImportException;
import at.wrk.coceso.niu.data.ExternalUser;

import java.util.Collection;

public interface ExternalUserParser {
    Collection<ExternalUser> parseExternalUsers(String csvData) throws ImportException;
}
