package at.wrk.geocode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LatLngTest {

  /**
   * Test of getLat and getLng methods, of class LatLng.
   */
  @Test
  public void testGet() {
    LatLng instance = new LatLng(48.20, 16.30);
    assertEquals(48.2, instance.getLat(), 0);
    assertEquals(16.30, instance.getLng(), 0);
  }

  /**
   * Test of distance method, of class LatLng.
   */
  @Test
  public void testDistance() {
    LatLng a = new LatLng(48.175, 16.30),
        b = new LatLng(48.178, 16.31),
        c = new LatLng(48.176, 16.305),
        d = new LatLng(48.176, -343.695);

    assertEquals(0, a.distance(a));
    assertEquals(0, b.distance(b));
    assertEquals(0, c.distance(c));
    assertEquals(0, d.distance(d));

    assertEquals(813, a.distance(b));
    assertEquals(813, b.distance(a));

    assertEquals(387, a.distance(c));
    assertEquals(387, c.distance(a));

    assertEquals(387, a.distance(d));
    assertEquals(387, d.distance(a));

    assertEquals(432, b.distance(c));
    assertEquals(432, c.distance(b));

    assertEquals(432, b.distance(d));
    assertEquals(432, d.distance(b));

    assertEquals(0, c.distance(d));
    assertEquals(0, d.distance(c));
  }

  /**
   * Test of boundsForDistance method, of class LatLng.
   */
  @Test
  public void testBoundsForDistance() {
    LatLng coord = new LatLng(48.2015, 16.34);
    Bounds bounds = coord.boundsForDistance(100);

    assertTrue(bounds.contains(coord));
    assertTrue(bounds.contains(new LatLng(48.2015, 16.339)));
    assertTrue(bounds.contains(new LatLng(48.2015, 16.341)));
    assertTrue(bounds.contains(new LatLng(48.2010, 16.340)));
    assertTrue(bounds.contains(new LatLng(48.2020, 16.340)));

    assertFalse(bounds.contains(new LatLng(48.2015, 16.3385)));
    assertFalse(bounds.contains(new LatLng(48.2015, 16.3415)));
    assertFalse(bounds.contains(new LatLng(48.2005, 16.340)));
    assertFalse(bounds.contains(new LatLng(48.2025, 16.340)));
  }

}
