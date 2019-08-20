package at.wrk.coceso.niu.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

/**
 * Combined identifier for imported users.
 */
public final class ExternalUserId {
    private final int personnelId;
    private final String lastname;
    private final String firstname;

    public ExternalUserId(final int personnelId, final String lastname, final String firstname) {
        this.personnelId = personnelId;
        this.lastname = lastname;
        this.firstname = firstname;
    }

    public int getPersonnelId() {
        return personnelId;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ExternalUserId)) {
            return false;
        }

        ExternalUserId that = (ExternalUserId) o;
        return personnelId == that.personnelId &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(firstname, that.firstname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personnelId, lastname, firstname);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("personnelId", personnelId)
                .append("lastname", lastname)
                .append("firstname", firstname)
                .toString();
    }
}
