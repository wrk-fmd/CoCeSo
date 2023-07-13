package at.wrk.coceso.alarm.text.service.normalizer;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TetraIssiNormalizerTest {
    private TetraIssiNormalizer sut;

    @Before
    public void init() {
        sut = new TetraIssiNormalizer();
    }

    @Test
    public void validIssi_returnIssi() {
        String issi = "2321009";

        String normalizedIssi = sut.normalize(issi);

        assertThat(normalizedIssi, equalTo("2321009"));
    }

    @Test
    public void issiWithBlanks_returnTrimmedIssi() {
        String issi = " 232 1009";

        String normalizedIssi = sut.normalize(issi);

        assertThat(normalizedIssi, equalTo("2321009"));
    }

    @Test
    public void emptyString_returnEmptyString() {
        String issi = "  ";

        String normalizedIssi = sut.normalize(issi);

        assertThat(normalizedIssi, equalTo(""));
    }

    @Test
    public void null_returnEmptyString() {
        String issi = null;

        String normalizedIssi = sut.normalize(issi);

        assertThat(normalizedIssi, equalTo(""));
    }

    @Test
    public void issiContainsInvalidCharacters_returnEmptyString() {
        String issi = "123aaa456";

        String normalizedIssi = sut.normalize(issi);

        assertThat(normalizedIssi, equalTo(""));
    }
}
