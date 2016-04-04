package at.wrk.coceso.entity.enums;

import java.util.Collection;
import java.util.EnumSet;
import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

  Dashboard,
  MLS(Dashboard),
  Kdt(MLS),
  Root(Kdt);

  private Collection<Authority> authorities;
  private final Authority[] children;

  Authority() {
    this.children = new Authority[]{};
  }

  Authority(Authority... children) {
    this.children = children;
  }

  public Collection<Authority> getAuthorities() {
    if (authorities == null) {
      authorities = EnumSet.of(this);
      for (Authority a : children) {
        authorities.addAll(a.getAuthorities());
      }
    }
    return authorities;
  }

  @Override
  public String getAuthority() {
    return this.name();
  }

}
