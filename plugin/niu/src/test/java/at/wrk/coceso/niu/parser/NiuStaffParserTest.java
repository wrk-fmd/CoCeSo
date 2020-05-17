package at.wrk.coceso.niu.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import at.wrk.coceso.dto.contact.ContactDto;
import at.wrk.coceso.parser.staff.ParsedStaffMember;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class NiuStaffParserTest {

    private NiuStaffParser sut;

    @Before
    public void init() {
        sut = new NiuStaffParser();
    }

    @Test
    public void validCsv_returnExternalUsers() {
        String csv =
            "Kategorie|DNr.|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschäftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n"
                +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        Collection<ParsedStaffMember> parsed = sut.parse(csv);

        assertEquals(1, parsed.size());

        ParsedStaffMember staffMember = parsed.stream().findFirst().orElse(null);
        assertNotNull(staffMember);

        assertEquals("Max", staffMember.getFirstname());
        assertEquals("Mustermann", staffMember.getFirstname());

        assertEquals(1, staffMember.getPersonnelId().size());
        assertThat(staffMember.getPersonnelId(), contains(1234));

        assertEquals(2, staffMember.getContacts().size());
        assertThat(staffMember.getContacts(), contains(phone("+43 (600) 1234"), phone("+43 (600) 1235")));
    }

    @Test
    public void oneLineHasInvalidData_returnValidData() {
        String csv =
            "Kategorie|DNr.|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschäftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n"
                +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n"
                +
                "NIU-Kontakt|invalid-id|Herr||Musterfrau|Matilde||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        Collection<ParsedStaffMember> parsed = sut.parse(csv);

        assertEquals(1, parsed.size());

        ParsedStaffMember staffMember = parsed.stream().findFirst().orElse(null);
        assertNotNull(staffMember);

        assertEquals("Max", staffMember.getFirstname());
        assertEquals("Mustermann", staffMember.getFirstname());

        assertEquals(1, staffMember.getPersonnelId().size());
        assertThat(staffMember.getPersonnelId(), contains(1234));

        assertEquals(2, staffMember.getContacts().size());
        assertThat(staffMember.getContacts(), contains(phone("+43 (600) 1234"), phone("+43 (600) 1235")));
    }

    @Test
    public void headerWithInvalidEncoding_returnPartOfData() {
        String csv =
            "Kategorie|DNr.|Anrede|Vorg.Titel|Nachname|Vorname|Weitere Vornamen|Nachg.Titel|Bereich|Straße|PLZ|Ort|Mitgliederstatus|Telefon geschäftlich|Telefon Privat|Telefon WRK|Handy privat|Handy geschxxxftlich|Handy WRK|e-mail privat|e-mail geschäftlich|e-mail WRK|Fax geschäftlich|Fax Privat|fax WRK|Notruf Pager WRK|Pager|Bereitschaft WRK|Tetra Funkgerät|\n"
                +
                "NIU-Kontakt|1234|Herr||Mustermann|Max||MSc|WRK\\LV WIEN\\KHD|Mustergasse 1/2|1010|Wien|AKTIV|||+43 (600) 1234|+43 (600) 1234|+43 (600) 1235||||max.mustermann@here.local||||000300||+43 (600) 1234||\n";

        Collection<ParsedStaffMember> parsed = sut.parse(csv);

        assertEquals(1, parsed.size());

        ParsedStaffMember staffMember = parsed.stream().findFirst().orElse(null);
        assertNotNull(staffMember);

        assertEquals("Max", staffMember.getFirstname());
        assertEquals("Mustermann", staffMember.getFirstname());

        assertEquals(1, staffMember.getPersonnelId().size());
        assertThat(staffMember.getPersonnelId(), contains(1234));

        assertEquals(1, staffMember.getContacts().size());
        assertThat(staffMember.getContacts(), contains(phone("+43 (600) 1234")));
    }

    private ContactDto phone(String number) {
        ContactDto contact = new ContactDto();
        contact.setType("phone");
        contact.setData(number);
        return contact;
    }
}
