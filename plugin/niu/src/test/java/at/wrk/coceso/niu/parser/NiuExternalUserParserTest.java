package at.wrk.coceso.niu.parser;

import at.wrk.coceso.importer.ImportException;
import at.wrk.coceso.niu.data.ExternalUser;
import at.wrk.coceso.niu.data.ExternalUserId;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class NiuExternalUserParserTest {
    private NiuExternalUserParser sut;

    @Before
    public void init() {
        sut = new NiuExternalUserParser();
    }

    @Test
    public void validCsv_returnExternalUsers() throws ImportException {
        String validCsv = "Kategorie|DNr.|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschäftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n" +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        Collection<ExternalUser> externalUsers = sut.parseExternalUsers(validCsv);

        ExternalUser expectedUser = new ExternalUser(new ExternalUserId(1234, "Mustermann", "Max"), ImmutableSet.of("+43 (600) 1234", "+43 (600) 1235"));
        assertThat(externalUsers, contains(expectedUser));
    }

    @Test(expected = ImportException.class)
    public void headerMissing_throwException() throws ImportException {
        String validCsv = "Kategorie|Firma|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschäftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n" +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        sut.parseExternalUsers(validCsv);
    }

    @Test
    public void oneLineHasInvalidData_returnValidData() throws ImportException {
        String validCsv = "Kategorie|DNr.|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschäftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n" +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n" +
                "NIU-Kontakt|invalid-id|Herr||Musterfrau|Matilde||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        Collection<ExternalUser> externalUsers = sut.parseExternalUsers(validCsv);

        ExternalUser expectedUser = new ExternalUser(new ExternalUserId(1234, "Mustermann", "Max"), ImmutableSet.of("+43 (600) 1234", "+43 (600) 1235"));
        assertThat(externalUsers, contains(expectedUser));
    }

    @Test
    public void headerWithInvalidEncoding_returnPartOfData() throws ImportException {
        String validCsv = "Kategorie|DNr.|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschxxxftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n" +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        Collection<ExternalUser> externalUsers = sut.parseExternalUsers(validCsv);

        ExternalUser expectedUser = new ExternalUser(new ExternalUserId(1234, "Mustermann", "Max"), ImmutableSet.of("+43 (600) 1234"));
        assertThat(externalUsers, contains(expectedUser));
    }
}