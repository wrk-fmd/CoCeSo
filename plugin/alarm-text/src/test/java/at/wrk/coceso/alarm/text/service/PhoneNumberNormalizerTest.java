package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.alarm.text.service.normalizer.PhoneNumberNormalizer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneNumberNormalizerTest {
    private PhoneNumberNormalizer sut;

    @Before
    public void init() {
        AlarmTextConfiguration alarmTextConfiguration = mock(AlarmTextConfiguration.class);
        when(alarmTextConfiguration.getValidPrefix()).thenReturn("+436");
        when(alarmTextConfiguration.getDefaultCountryCode()).thenReturn("+43");
        sut = new PhoneNumberNormalizer(alarmTextConfiguration);
    }

    @Test
    public void numberWithWhitespaces_withespacesAreRemoved() {
        String normalized = sut.normalize("+43 678 933 543");

        assertThat(normalized, equalTo("+43678933543"));
    }

    @Test
    public void numberWithSpecialCharacters_allSpecialCharactersRemovedExceptPlus() {
        String normalized = sut.normalize("+43 (678) 933 - 289 /#\\?");

        assertThat(normalized, equalTo("+43678933289"));
    }

    @Test
    public void numberStartingWithSingleZero_startingZeroReplaced() {
        String normalized = sut.normalize("0678 933 368");

        assertThat(normalized, equalTo("+43678933368"));
    }

    @Test
    public void numberStartingWithDoubleZero_startingZeroReplaced() {
        String normalized = sut.normalize("00 43 678 933 361");

        assertThat(normalized, equalTo("+43678933361"));
    }

    @Test
    public void null_emptyString() {
        String normalized = sut.normalize(null);

        assertThat(normalized, equalTo(""));
    }

    @Test
    public void numberWithForeignCarrier_emptyString() {
        String normalized = sut.normalize("+49 678 933 543");

        assertThat(normalized, equalTo(""));
    }

    @Test
    public void numberNotMatchingValidPrefix_emptyString() {
        String normalized = sut.normalize("+43 128 933 543");

        assertThat(normalized, equalTo(""));
    }

    @Test
    public void invalidNumber_emptyString() {
        String normalized = sut.normalize("98234");

        assertThat(normalized, equalTo(""));
    }
}