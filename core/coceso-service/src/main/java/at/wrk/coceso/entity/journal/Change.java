package at.wrk.coceso.entity.journal;

import at.wrk.fmd.mls.hibernate.json.PostgresJsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@TypeDef(name = "json", typeClass = PostgresJsonType.class)
public class Change {

    private String key;

    @Type(type = "json")
    private Object oldValue;

    @Type(type = "json")
    private Object newValue;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("key", key)
                .append("oldValue", oldValue)
                .append("newValue", newValue)
                .toString();
    }
}
