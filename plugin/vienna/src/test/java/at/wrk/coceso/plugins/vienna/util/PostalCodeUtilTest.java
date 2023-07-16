package at.wrk.coceso.plugins.vienna.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PostalCodeUtilTest {
    private PostalCodeUtil sut;

    @Before
    public void init() {
        sut = new PostalCodeUtil();
    }

    @Test
    public void validNumberReturnsPostalCode() {
        Optional<String> postalCode = sut.createPostalCodeForDistrictNumber("5");

        assertThat(postalCode, isPresentAnd(equalTo("1050")));
    }

    @Test
    public void validTwoDigitNumberReturnsPostalCode() {
        Optional<String> postalCode = sut.createPostalCodeForDistrictNumber("12");

        assertThat(postalCode, isPresentAnd(equalTo("1120")));
    }

    @Test
    public void negativeNumberReturnsEmptyOptional() {
        Optional<String> postalCode = sut.createPostalCodeForDistrictNumber("-1");

        assertThat(postalCode, isEmpty());
    }

    @Test
    public void invalidNumberReturnsEmptyOptional() {
        Optional<String> postalCode = sut.createPostalCodeForDistrictNumber("foo");

        assertThat(postalCode, isEmpty());
    }

    @Test
    public void nullInputReturnsEmptyOptional() {
        Optional<String> postalCode = sut.createPostalCodeForDistrictNumber(null);

        assertThat(postalCode, isEmpty());
    }

    @Test
    public void outOfRangeInputReturnsEmptyOptional() {
        Optional<String> postalCode = sut.createPostalCodeForDistrictNumber("24");

        assertThat(postalCode, isEmpty());
    }
}
