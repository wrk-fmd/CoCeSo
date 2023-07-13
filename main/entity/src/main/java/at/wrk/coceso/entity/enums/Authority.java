package at.wrk.coceso.entity.enums;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum Authority implements GrantedAuthority {

    Dashboard,
    MLS(Dashboard),
    Kdt(MLS),
    Root(Kdt);

    private final Set<Authority> authorities;

    Authority() {
        this(new Authority[]{});
    }

    Authority(Authority... children) {
        this.authorities = resolveGrantedAuthorities(this, Set.of(children));
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getAuthority() {
        return this.name();
    }

    private static Set<Authority> resolveGrantedAuthorities(final Authority startingPoint, final Set<Authority> children) {

        Set<Authority> resolvedAuthorities = Sets.newHashSet(startingPoint);
        for (Authority child : children) {
            resolvedAuthorities.addAll(child.getAuthorities());
        }

        return Set.copyOf(resolvedAuthorities);
    }
}
