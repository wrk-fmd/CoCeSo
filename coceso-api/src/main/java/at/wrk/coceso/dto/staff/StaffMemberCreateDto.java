package at.wrk.coceso.dto.staff;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.dto.contact.ContactDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class StaffMemberCreateDto {

    @NotNull
    @Length(min = 1, max = Lengths.STAFF_FIRSTNAME)
    private String firstname;

    @NotNull
    @Length(min = 1, max = Lengths.STAFF_LASTNAME)
    private String lastname;

    @Length(max = Lengths.STAFF_INFO)
    private String info;

    private Set<Integer> personnelId;
    private Collection<ContactDto> contacts;
}
