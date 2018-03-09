package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.Bounds;
import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.address.Address;
import at.wrk.geocode.address.AddressNumber;
import org.junit.Test;
import static org.junit.Assert.*;

public class ViennaGeocoderTest {

  /**
   * Test of geocode method, of class ViennaGeocoder.
   */
  @Test
  public void testGeocodeSuccess() {
    testGeocode("Neubaugasse", "7/3", 1070, null, new Bounds(new LatLng(48.198, 16.348), new LatLng(48.199, 16.349)));
    testGeocode("Neubaugasse", "7/3", 1070, "Wien", new Bounds(new LatLng(48.198, 16.348), new LatLng(48.199, 16.349)));
    testGeocode("Neubaugasse", "7/3", null, null, new Bounds(new LatLng(48.198, 16.348), new LatLng(48.199, 16.349)));
    testGeocode("Mozartstraße", "1", 4020, null, null);
    testGeocode("Mozartstraße", "1", null, "Linz", null);
    testGeocode("Test", "1", 1130, "Wien", null);
  }

  private void testGeocode(String street, String number, Integer postCode, String city, Bounds expected) {
    ViennaGeocoder instance = new ViennaGeocoder();
    Address address = new Address() {
      @Override
      public String getStreet() {
        return street;
      }

      @Override
      public String getIntersection() {
        return null;
      }

      @Override
      public Address.Number getNumber() {
        return new AddressNumber(number);
      }

      @Override
      public Integer getPostCode() {
        return postCode;
      }

      @Override
      public String getCity() {
        return city;
      }
    };

    LatLng result = instance.geocode(address);

    if (expected == null) {
      assertNull("Unexpected result for " + address.getInfo(", "), result);
    } else {
      assertTrue("Wrong result for " + address.getInfo(", "), expected.contains(result));
    }
  }

  /**
   * Test of reverse method, of class ViennaGeocoder.
   */
  @Test
  public void testReverse() {
    ViennaGeocoder instance = new ViennaGeocoder();
    ReverseResult<Address> result;

    result = instance.reverse(new LatLng(48.202813, 16.342256));
    assertNotNull("Expected a result at Kandlgasse, 1070", result);
    assertEquals("Kandlgasse", result.result.getStreet());
    assertNotNull("Expected a post code at Kandlgasse, 1070", result.result.getPostCode());
    assertEquals(1070, (int) result.result.getPostCode());
    Integer number = result.result.getNumber().getFrom();
    assertNotNull("Expected a number at Kandlgasse, 1070", number);
    assertTrue("Expected number to be in range 19-24", number >= 19 && number <= 24);
    assertTrue("Expected distance to be at most 50 meters", result.dist < 50);

    result = instance.reverse(new LatLng(48.32, 16.19));
    assertNotNull("Expected a result near the border of Vienna", result);
    assertNotNull("Expected a post code for request outside of Vienna", result.result.getPostCode());
    assertEquals(1140, (int) result.result.getPostCode());
    assertTrue("Expected result to be far away from coordinates", result.dist > 5000);

    result = instance.reverse(new LatLng(48.18, 16.10));
    assertNull("Expected no result for request at Pressbaum", result);
  }

}
