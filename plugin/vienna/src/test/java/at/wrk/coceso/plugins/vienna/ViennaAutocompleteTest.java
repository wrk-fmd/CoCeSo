package at.wrk.coceso.plugins.vienna;

import java.io.IOException;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ViennaAutocompleteTest {

  private ViennaAutocomplete instance;

  @Before
  public void setUp() {
    try {
      instance = new ViennaAutocomplete();
    } catch (IOException ex) {
      fail("IOException on loading ViennaAutocomplete");
    }
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
    if (results.size() > max) {
      fail("Too many results for ViennaAutocomplete.getContaining for search string " + str);
    }
  }

}
