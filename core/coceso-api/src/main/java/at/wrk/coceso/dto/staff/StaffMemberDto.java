package at.wrk.coceso.dto.staff;

import at.wrk.coceso.dto.contact.ContactDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class StaffMemberDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String info;
    private Collection<Integer> personnelId;
    private Collection<ContactDto> contacts;
}
