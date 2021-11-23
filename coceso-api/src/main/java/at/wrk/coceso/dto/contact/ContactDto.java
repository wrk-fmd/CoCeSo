package at.wrk.coceso.dto.contact;

import at.wrk.coceso.dto.Lengths;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    @NotNull
    @Length(max = Lengths.CONTACT_TYPE)
    private String type;

    @NotNull
    @Length(min = 1, max = Lengths.CONTACT_DATA)
    private String data;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContactDto that = (ContactDto) o;
        return Objects.equals(type, that.type) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, data);
    }
}
