package at.wrk.coceso.alarm.text.configuration;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AlarmTextConfigurationTest {
    @Test
    public void getSmsGatewayUri_noSmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, null, null, null, null);

        URI smsGatewayUri = sut.getSmsGatewayUri();

        assertThat("Expected null for SMS GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getSmsGatewayUri_emptySmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration("", null, null, null, null, null);

        URI smsGatewayUri = sut.getSmsGatewayUri();

        assertThat("Expected null for SMS GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getSmsGatewayUri_invalidSmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration("::invalid-uri", null, null, null, null, null);

        URI smsGatewayUri = sut.getSmsGatewayUri();

        assertThat("Expected null for SMS GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getSmsGatewayUri_ftpSmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration("ftp://some.server.invalid/", null, null, null, null, null);

        URI smsGatewayUri = sut.getSmsGatewayUri();

        assertThat("Expected null for SMS GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getSmsGatewayUri_httpSmsGatewayUriConfigured_returnUri() {
        String uriString = "http://some.server.invalid/";
        AlarmTextConfiguration sut = new AlarmTextConfiguration(uriString, null, null, null, null, null);

        URI smsGatewayUri = sut.getSmsGatewayUri();

        URI expectedUri = URI.create(uriString);
        assertThat("Expected matching SMS GW URI.", smsGatewayUri, equalTo(expectedUri));
    }

    @Test
    public void getSmsGatewayUri_httpsSmsGatewayUriConfigured_returnUri() {
        String uriString = "https://some.server.invalid/";
        AlarmTextConfiguration sut = new AlarmTextConfiguration(uriString, null, null, null, null, null);

        URI smsGatewayUri = sut.getSmsGatewayUri();

        URI expectedUri = URI.create(uriString);
        assertThat("Expected matching SMS GW URI.", smsGatewayUri, equalTo(expectedUri));
    }

    @Test
    public void getTetraGatewayUri_noSmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, null, null, null, null);

        URI smsGatewayUri = sut.getTetraGatewayUri();

        assertThat("Expected null for TETRA GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getTetraGatewayUri_emptySmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, "", null, null, null);

        URI smsGatewayUri = sut.getTetraGatewayUri();

        assertThat("Expected null for TETRA GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getTetraGatewayUri_invalidSmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, "::invalid-uri", null, null, null);

        URI smsGatewayUri = sut.getTetraGatewayUri();

        assertThat("Expected null for TETRA GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getTetraGatewayUri_ftpSmsGatewayUriConfigured_returnNull() {
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, "ftp://some.server.invalid/", null, null, null);

        URI smsGatewayUri = sut.getTetraGatewayUri();

        assertThat("Expected null for TETRA GW URI.", smsGatewayUri, equalTo(null));
    }

    @Test
    public void getTetraGatewayUri_httpSmsGatewayUriConfigured_returnUri() {
        String uriString = "http://some.server.invalid/";
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, uriString, null, null, null);

        URI smsGatewayUri = sut.getTetraGatewayUri();

        URI expectedUri = URI.create(uriString);
        assertThat("Expected matching TETRA GW URI.", smsGatewayUri, equalTo(expectedUri));
    }

    @Test
    public void getTetraGatewayUri_httpsSmsGatewayUriConfigured_returnUri() {
        String uriString = "https://some.server.invalid/";
        AlarmTextConfiguration sut = new AlarmTextConfiguration(null, null, uriString, null, null, null);

        URI smsGatewayUri = sut.getTetraGatewayUri();

        URI expectedUri = URI.create(uriString);
        assertThat("Expected matching TETRA GW URI.", smsGatewayUri, equalTo(expectedUri));
    }
}
