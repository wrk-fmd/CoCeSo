package at.wrk.coceso.entity.point;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class AddressPointParserTest {
    @Test
    public void parseAddressWithNumber_fullNumberIsReturned() {
        AddressPoint addressPoint = AddressPointParser.parseFromString("Neubaugasse 1-3/2/3/4 Meier");

        assertThat(addressPoint.getStreet(), equalTo("Neubaugasse"));
        assertThat(addressPoint.getNumber().getFrom(), equalTo(1));
        assertThat(addressPoint.getNumber().getTo(), equalTo(3));
        assertThat(addressPoint.getNumber().getBlock(), equalTo("2"));
        assertThat(addressPoint.getNumber().getDetails(), equalTo("3/4 Meier"));
    }

    @Test
    public void parseAddressWithShortNumber_numberIsReturned() {
        AddressPoint addressPoint = AddressPointParser.parseFromString("Neubaugasse 1///EHF");

        assertThat(addressPoint.getStreet(), equalTo("Neubaugasse"));
        assertThat(addressPoint.getNumber().getFrom(), equalTo(1));
        assertThat(addressPoint.getNumber().getTo(), nullValue());
        assertThat(addressPoint.getNumber().getBlock(), nullValue());
        assertThat(addressPoint.getNumber().getDetails(), equalTo("/EHF"));
    }

    @Test
    public void parseAddressWithIntersection_intersectionIsReturned() {
        AddressPoint addressPoint = AddressPointParser.parseFromString("Neubaugasse # Stanislausgasse");

        assertThat(addressPoint.getStreet(), equalTo("Neubaugasse"));
        assertThat(addressPoint.getIntersection(), equalTo("Stanislausgasse"));
    }

    @Test
    public void parseAddressWithIntersectionAndCity_intersectionIsReturned() {
        AddressPoint addressPoint = AddressPointParser.parseFromString("Neubaugasse # Stanislausgasse\n1234 Wien");

        assertThat(addressPoint.getStreet(), equalTo("Neubaugasse"));
        assertThat(addressPoint.getIntersection(), equalTo("Stanislausgasse"));
        assertThat(addressPoint.getCity(), equalTo("Wien"));
        assertThat(addressPoint.getPostCode(), equalTo(1234));
    }
}