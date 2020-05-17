package at.wrk.coceso.auth;

import at.wrk.coceso.entity.enums.UnitType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public enum AccessLevel {

    // Access based on global role
    CONCERN_READ("MLS"),
    CONCERN_EDIT("MLS"),
    CONCERN_CLOSE("Kdt"),
    CONTAINER_READ("MLS"),
    CONTAINER_EDIT("MLS"),
    INCIDENT_READ("MLS"),
    INCIDENT_EDIT("MLS"),
    JOURNAL_READ("MLS"),
    JOURNAL_CREATE("MLS"),
    PATIENT_READ("MLS"),
    PATIENT_EDIT("MLS"),
    TASK_EDIT("MLS"),
    UNIT_READ("MLS"),
    UNIT_EDIT("MLS"),
    UNIT_ASSIGN("MLS"),
    STAFF_READ("MLS"),
    STAFF_EDIT("MLS"),

    // Access based on specific unit types

    // Access for all unit types
    UNIT_SELF();

    private final Set<String> roles;
    private final Set<UnitType> types;

    AccessLevel() {
        this(null, null);
    }

    AccessLevel(String... roles) {
        this(roles, null);
    }

    AccessLevel(UnitType... types) {
        this(null, types);
    }

    /**
     * Most generic AccessLevel constructor
     *
     * @param roles Global authorities granting this AccessLevel (per user), or null if granted for any role
     * @param types Required UnitTypes for this AccessLevel concern-wide, or null for no concern-wide access
     */
    AccessLevel(String[] roles, UnitType[] types) {
        this.roles = roles != null ? Arrays.stream(roles).collect(Collectors.toUnmodifiableSet()) : null;
        this.types = types != null ? Arrays.stream(types).collect(Collectors.toUnmodifiableSet()) : null;
    }

    public boolean isGrantedForRoles(Collection<? extends GrantedAuthority> roles) {
        return this.roles != null && roles.stream().map(GrantedAuthority::getAuthority).anyMatch(this.roles::contains);
    }

    public boolean isGrantedForTypes(Collection<UnitType> types) {
        return this.types != null && types.stream().anyMatch(this.types::contains);
    }

    public boolean isGrantedForAssigned() {
        return roles == null && types == null;
    }
}
