package at.wrk.coceso.plugins.vienna;

import at.wrk.coceso.plugins.vienna.util.PostalCodeUtil;
import at.wrk.coceso.plugins.vienna.util.ViennaStreetParser;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ViennaAutocompleteComponentTest {

    private ViennaAutocomplete instance;

    @Before
    public void setUp() {
        instance = new ViennaAutocomplete(new StreetnameResourceProvider(), new ViennaStreetParser(new PostalCodeUtil()));
        instance.onInit();
        Awaitility.await()
                .until(instance::isInitialized);
    }

    @After
    public void tearDown() {
        instance = null;
    }

    @Test
    public void testStart() {
        testStart("nottendorfer");
        testStart("wie");
        testStart("lange");
    }

    private void testStart(String str) {
        Collection<String> results = instance.getStartCollection(str);
        if (results.isEmpty()) {
            fail("No results for ViennaAutocomplete.getStart for search string " + str);
        }
        results.stream()
                .filter(r -> !r.toLowerCase().startsWith(str))
                .forEach(r -> fail("Unexpected result " + r + " for search string " + str));
    }

    @Test
    public void testContaining() {
        testContaining("not");
        testContaining("wie");
        testContaining("lange");
    }

    private void testContaining(String str) {
        Collection<String> results = instance.getContainingCollection(str, null);
        if (results.isEmpty()) {
            fail("No results for ViennaAutocomplete.getContaining for search string " + str);
        }

        results.stream()
                .filter(r -> r.toLowerCase().startsWith(str) || !r.toLowerCase().contains(str))
                .forEach(r -> fail("Unexpected result " + r + " for search string " + str));
    }

    @Test
    public void testContainingMaxLength() {
        testContaining("straße", 10);
        testContaining("gasse", 20);
    }

    private void testContaining(String str, int max) {
        Collection<String> results = instance.getContainingCollection(str, max);
        assertThat("Expected that exactly the maximum result is returned.", results.size(), equalTo(max));
    }
}
