package at.wrk.coceso.niu.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.Set;

public class ExternalUser {
    private final ExternalUserId externalUserId;
    private final Set<String> telephoneNumbers;

    public ExternalUser(final ExternalUserId externalUserId, final Set<String> telephoneNumbers) {
        this.externalUserId = externalUserId;
        this.telephoneNumbers = telephoneNumbers;
    }

    public ExternalUserId getExternalUserId() {
        return externalUserId;
    }

    public Set<String> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalUser)) {
            return false;
        }
        ExternalUser that = (ExternalUser) o;
        return Objects.equals(externalUserId, that.externalUserId) &&
                Objects.equals(telephoneNumbers, that.telephoneNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalUserId, telephoneNumbers);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("externalUserId", externalUserId)
                .append("telephoneNumbers", telephoneNumbers)
                .toString();
    }
}
