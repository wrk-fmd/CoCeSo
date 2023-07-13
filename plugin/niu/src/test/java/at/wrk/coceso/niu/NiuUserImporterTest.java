package at.wrk.coceso.niu;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.niu.data.ExternalUser;
import at.wrk.coceso.niu.data.ExternalUserId;
import at.wrk.coceso.niu.parser.ExternalUserParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NiuUserImporterTest {
    private NiuUserImporter sut;
    private ExternalUserParser externalUserParser;
    private static int count;

    @BeforeAll
    static void initClass() {
        count = 42;
    }

    @BeforeEach
    void init() {
        externalUserParser = mock(ExternalUserParser.class);
        sut = new NiuUserImporter(externalUserParser);
    }

    @Test
    void conflictingUsersInDatabase_runImporter_noException() {
        String csvData = "mocked-csv-data";

        ExternalUser importedUser = new ExternalUser(new ExternalUserId(2, "created", "created"), Set.of());
        when(externalUserParser.parseExternalUsers(csvData)).thenReturn(Set.of(importedUser));

        Collection<User> users = sut.updateUsers(csvData, List.of(createSampleUser(), createSampleUser()));

        assertThat(users, hasItem(Matchers.<User>allOf(
                hasProperty("personnelId", equalTo(2)),
                hasProperty("lastname", equalTo("created")),
                hasProperty("firstname", equalTo("created"))
        )));
    }

    @Test
    void userWithoutDataInDatabase_runImporter_returnNewUser() {
        String csvData = "mocked-csv-data";

        ExternalUser importedUser = new ExternalUser(new ExternalUserId(0, "", ""), Set.of("telephone-number"));
        when(externalUserParser.parseExternalUsers(csvData)).thenReturn(Set.of(importedUser));

        Collection<User> users = sut.updateUsers(csvData, List.of(createEmptyUser()));

        assertThat(users, hasItem(Matchers.<User>allOf(
                hasProperty("personnelId", equalTo(0)),
                hasProperty("lastname", equalTo("")),
                hasProperty("firstname", equalTo("")),
                hasProperty("id", nullValue())
        )));
    }

    private User createEmptyUser() {
        User user = new User(0, "", "");
        user.setId(count);
        return user;
    }

    private User createSampleUser() {
        User user = new User(1, "lastname", "firstname");
        user.setId(count++);
        return user;
    }
}
