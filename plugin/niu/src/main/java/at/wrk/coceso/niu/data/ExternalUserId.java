package at.wrk.coceso.niu.data;

import at.wrk.coceso.niu.NiuToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * Combined identifier for imported users.
 */
public final class ExternalUserId {
    private final int personellId;
    private final String lastname;
    private final String firstname;

    public ExternalUserId(final int personellId, final String lastname, final String firstname) {
        this.personellId = personellId;
        this.lastname = lastname;
        this.firstname = firstname;
    }

    public int getPersonellId() {
        return personellId;
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
        return personellId == that.personellId &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(firstname, that.firstname);
    }

    @Override
    public int hashCode() {

        return Objects.hash(personellId, lastname, firstname);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, NiuToStringStyle.STYLE)
                .append("personellId", personellId)
                .append("lastname", lastname)
                .append("firstname", firstname)
                .toString();
    }
}
