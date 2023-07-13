package at.wrk.coceso.entity.enums;

import org.junit.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class AuthorityTest {

  /**
   * Test of getAuthorities method, of class Authority.
   */
  @Test
  public void testAuthorities() {
    testAuthorities(Authority.Dashboard, EnumSet.of(Authority.Dashboard));
    testAuthorities(Authority.MLS, EnumSet.of(Authority.Dashboard, Authority.MLS));
    testAuthorities(Authority.Kdt, EnumSet.of(Authority.Dashboard, Authority.MLS, Authority.Kdt));
    testAuthorities(Authority.Root, EnumSet.of(Authority.Dashboard, Authority.MLS, Authority.Kdt, Authority.Root));
  }

  private void testAuthorities(Authority granted, Set<Authority> expected) {
    Collection<Authority> authorities = granted.getAuthorities();
    for (Authority role : Authority.values()) {
      assertEquals(String.format("Failure testing role %s containing role %s", granted, role),
          expected.contains(role), authorities.contains(role));
    }
  }
}
