package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.AddressNumber;
import at.wrk.geocode.address.ImmutableAddress;
import at.wrk.geocode.address.ImmutableAddressNumber;
import at.wrk.geocode.util.AddressMatcher;
import at.wrk.geocode.util.AddressNumberMatcher;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ViennaGeocoderTest {

    private ViennaGeocoder sut;

    @Before
    public void init() {
        // TODO create correct unit test with mocked dependencies
        AddressMatcher addressMatcher = new AddressMatcher(LevenshteinDistance.getDefaultInstance(), new AddressNumberMatcher());

        ViennaGeocoderConfiguration viennaGeocoderConfiguration = new ViennaGeocoderConfiguration();
        RestTemplate restTemplate = viennaGeocoderConfiguration.createRestTemplate();

        sut = new ViennaGeocoder(addressMatcher, restTemplate);
    }

    @Test
    public void addressWithoutCity() {
        ImmutableAddress address = createAddress("Neubaugasse", "7/3", 1070, null);

        LatLng result = sut.geocode(address);

        Bounds expectedBounds = new Bounds(new LatLng(48.198, 16.348), new LatLng(48.199, 16.349));
        assertThatResultIsInBounds(expectedBounds, result);
    }

    @Test
    public void addressWithCity() {
        ImmutableAddress address = createAddress("Neubaugasse", "7/3", 1070, "Wien");

        LatLng result = sut.geocode(address);

        Bounds expectedBounds = new Bounds(new LatLng(48.198, 16.348), new LatLng(48.199, 16.349));
        assertThatResultIsInBounds(expectedBounds, result);
    }

    @Test
    public void addressWithoutPostalCode() {
        ImmutableAddress address = createAddress("Neubaugasse", "7/3", null, null);

        LatLng result = sut.geocode(address);

        Bounds expectedBounds = new Bounds(new LatLng(48.198, 16.348), new LatLng(48.199, 16.349));
        assertThatResultIsInBounds(expectedBounds, result);
    }

    @Test
    public void addressWithPostalCodeOutsideOfVienna_noResult() {
        ImmutableAddress address = createAddress("Mozartstraße", "1", 4020, null);

        LatLng result = sut.geocode(address);

        assertThatEmptyResultIsReturned(result);
    }

    @Test
    public void addressWithCityOutsideOfVienna_noResult() {
        ImmutableAddress address = createAddress("Mozartstraße", "1", null, "Linz");

        LatLng result = sut.geocode(address);

        assertThatEmptyResultIsReturned(result);
    }

    @Test
    public void invalidAddressInVienna_noResult() {
        ImmutableAddress address = createAddress("Invalid", "1", 1130, null);

        LatLng result = sut.geocode(address);

        assertThatEmptyResultIsReturned(result);
    }

    /**
     * Test of reverse method, of class ViennaGeocoder.
     */
    @Test
    public void testReverse() {
        ReverseResult<ImmutableAddress> result;

        result = sut.reverse(new LatLng(48.202813, 16.342256));
        assertThat("Expected a result at Kandlgasse, 1070", result, notNullValue());
        assertThat(result.result.getStreet(), equalTo("Kandlgasse"));
        assertThat("Expected a post code at Kandlgasse, 1070", result.result.getPostCode(), notNullValue());
        assertThat(result.result.getPostCode(), equalTo(1070));
        Integer number = result.result.getNumber().getFrom();
        assertThat("Expected a number at Kandlgasse, 1070", number, notNullValue());
        assertThat("Expected number to be in range 19-24", number, both(greaterThanOrEqualTo(19)).and(lessThanOrEqualTo(24)));
        assertThat("Expected distance to be at most 50 meters", result.dist, lessThanOrEqualTo(50));

        result = sut.reverse(new LatLng(48.32, 16.19));
        assertThat("Expected a result near the border of Vienna", result, notNullValue());
        assertThat("Expected a post code for request outside of Vienna", result.result.getPostCode(), notNullValue());
        assertThat(result.result.getPostCode(), equalTo(1140));
        assertThat("Expected result to be far away from coordinates", result.dist, greaterThan(5000));

        result = sut.reverse(new LatLng(48.18, 16.10));
        assertThat("Expected no result for request at Pressbaum", result, nullValue());
    }

    private ImmutableAddress createAddress(final String street, final String number, final Integer postCode, final String city) {
        return new ImmutableAddress(
                street,
                null,
                ImmutableAddressNumber.createFromAddressNumber(new AddressNumber(number)),
                postCode,
                city);
    }

    private void assertThatResultIsInBounds(final Bounds expected, final LatLng result) {
        assertThat("Wrong result returned", expected.contains(result), equalTo(true));
    }

    private void assertThatEmptyResultIsReturned(final LatLng result) {
        assertThat("Expected empty result", result, nullValue());
    }
}
