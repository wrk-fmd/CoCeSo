package at.wrk.coceso.entity;

import org.junit.Assert;
import org.junit.Test;

public class ConcernTest {

  @Test
  public void equalsTestReflexive() {
    Concern t = new Concern();
    Assert.assertTrue(t.equals(t));
  }

  @Test
  public void equalsTestId() {
    Concern t = new Concern(15), u = new Concern(15);
    Assert.assertTrue(t.equals(u));
    Assert.assertTrue(u.equals(t));
  }

  @Test
  public void notEqualsType() {
    Assert.assertFalse(new Concern(20).equals(new Object()));
  }

  @Test
  public void notEqualsNull() {
    Concern t = new Concern(37), u = null;
    Assert.assertFalse(t.equals(u));
  }

  @Test
  public void notEqualsEmptyId() {
    Assert.assertFalse(new Concern().equals(new Concern()));
  }

  @Test
  public void notEqualsEmptyId1() {
    Concern t = new Concern(), u = new Concern(12);
    Assert.assertFalse(t.equals(u));
    Assert.assertFalse(u.equals(t));
  }

  @Test
  public void notEqualsDifferentId() {
    Concern t = new Concern(7), u = new Concern(8);
    Assert.assertFalse(t.equals(u));
    Assert.assertFalse(u.equals(t));
  }

}
