package at.wrk.coceso.entity.enums;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum AccessLevel {

  Client(false, UnitType.Portable, UnitType.Officer),
  PatadminRoot(Authority.MLS),
  PatadminRegistration(null, new UnitType[]{UnitType.Triage}, null, new AccessLevel[]{PatadminRoot}),
  PatadminSettings(PatadminRegistration),
  PatadminPostprocessing(null, new UnitType[]{UnitType.Postprocessing}, null, new AccessLevel[]{PatadminRoot}),
  PatadminInfo(null, new UnitType[]{UnitType.Info}, null, new AccessLevel[]{PatadminRoot}),
  Patadmin(PatadminRegistration, PatadminPostprocessing, PatadminInfo, PatadminRoot),
  Edit(Authority.MLS),
  Dashboard(Authority.Dashboard),
  Report(Authority.MLS),
  Main(Authority.MLS),
  CloseConcern(Authority.Kdt),
  Home(Main, Dashboard, Patadmin),
  Root(Authority.Root);

  private final Collection<Authority> roles;
  private final Map<UnitType, Boolean> types;

  AccessLevel(Authority... authorities) {
    this(authorities, null, null, null);
  }

  AccessLevel(boolean concernwide, UnitType... types) {
    this(null, concernwide ? types : null, concernwide ? null : types, null);
  }

  AccessLevel(AccessLevel... inherit) {
    this(null, null, null, inherit);
  }

  /**
   * Most generic AccessLevel constructor
   *
   * @param authorities Global authorities granting this AccessLevel (per user)
   * @param concerntypes UnitTypes granting this AccessLevel concern-wide (per unit)
   * @param localtypes UnitTypes granting this AccessLevel locally (only directly assigned incidents/patients)
   * @param inherit List of AccessLevels inherited. If inherited level is granted, this level is granted too.
   */
  AccessLevel(Authority[] authorities, UnitType[] concerntypes, UnitType[] localtypes, AccessLevel[] inherit) {
    roles = authorities == null ? EnumSet.noneOf(Authority.class) : EnumSet.copyOf(Arrays.asList(authorities));
    types = new HashMap<>();
    if (localtypes != null) {
      for (UnitType type : localtypes) {
        types.put(type, false);
      }
    }
    if (concerntypes != null) {
      for (UnitType type : concerntypes) {
        types.put(type, true);
      }
    }

    if (inherit != null) {
      for (AccessLevel level : inherit) {
        roles.addAll(level.roles);
        level.types.forEach((type, value) -> {
          if (value) {
            types.put(type, true);
          } else {
            types.putIfAbsent(type, false);
          }
        });
      }
    }
  }

  public boolean isGrantedFor(Collection<? extends GrantedAuthority> authorities) {
    return roles.stream().anyMatch(authorities::contains);
  }

  public boolean isGrantedFor(UnitType type, boolean local) {
    Boolean grantedFor = types.get(type);
    return grantedFor != null && (grantedFor || local);
  }

  public boolean allowConcernWide() {
    return types.values().stream().anyMatch(v -> v);
  }

  public Set<UnitType> getTypes() {
    return types.keySet();
  }

  public static AccessLevel valueOf(Object level) {
    if (level instanceof AccessLevel) {
      return (AccessLevel) level;
    }
    if (level instanceof String) {
      return valueOf((String) level);
    }
    throw new IllegalArgumentException();
  }

}
