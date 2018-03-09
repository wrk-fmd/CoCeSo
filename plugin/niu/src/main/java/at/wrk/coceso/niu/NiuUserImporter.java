package at.wrk.coceso.niu;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.importer.ImportException;
import at.wrk.coceso.importer.UserImporter;
import at.wrk.coceso.niu.data.ExternalUser;
import at.wrk.coceso.niu.data.ExternalUserId;
import at.wrk.coceso.niu.parser.ExternalUserParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NiuUserImporter implements UserImporter {

    private final ExternalUserParser parser;

    @Autowired
    public NiuUserImporter(final ExternalUserParser parser) {
        this.parser = parser;
    }

    @Override
    public Collection<User> updateUsers(final String csvData, final Collection<User> existing) throws ImportException {
        // Put all existing users into a cache, indexed with personnel ID, lastname and firstname
        // Last occurence of combination will be kept
        Map<ExternalUserId, User> cache = existing.stream().collect(Collectors.toMap(
                existingUser -> new ExternalUserId(existingUser.getPersonnelId(), existingUser.getLastname(), existingUser.getFirstname()),
                Function.identity()));

        Collection<ExternalUser> parsedUsers = parser.parseExternalUsers(csvData);

        return parsedUsers
                .stream()
                .map(externalUser -> createOrUpdateUser(cache, externalUser))
                .collect(Collectors.toList());
    }

    private static User createOrUpdateUser(final Map<ExternalUserId, User> existingUsers, final ExternalUser externalUser) {
        ExternalUserId externalUserId = externalUser.getExternalUserId();
        User updatedUser = existingUsers.get(externalUserId);

        if (updatedUser == null) {
            updatedUser = new User(externalUserId.getPersonellId(), externalUserId.getLastname(), externalUserId.getFirstname());
        }

        updatedUser.setContact(getContactString(externalUser));
        return updatedUser;
    }

    private static String getContactString(final ExternalUser externalUser) {
        return String.join("\n", externalUser.getTelephoneNumbers());
    }
}
