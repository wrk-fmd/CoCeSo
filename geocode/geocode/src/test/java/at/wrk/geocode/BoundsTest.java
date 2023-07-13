package at.wrk.geocode;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoundsTest {

  /**
   * Test of contains method, of class Bounds.
   */
  @Test
  public void testContains() {
    Bounds bounds = new Bounds(new LatLng(48, 16), new LatLng(48.5, 16.3));

    assertTrue(bounds.contains(new LatLng(48, 16)));
    assertTrue(bounds.contains(new LatLng(48, 16.3)));
    assertTrue(bounds.contains(new LatLng(48.5, 16)));
    assertTrue(bounds.contains(new LatLng(48.5, 16.3)));
    assertTrue(bounds.contains(new LatLng(48.25, 16.15)));

    assertFalse(bounds.contains(new LatLng(47.9, 16.2)));
    assertFalse(bounds.contains(new LatLng(48.6, 16.2)));
    assertFalse(bounds.contains(new LatLng(48.2, 15.9)));
    assertFalse(bounds.contains(new LatLng(48.2, 16.4)));
  }

}
