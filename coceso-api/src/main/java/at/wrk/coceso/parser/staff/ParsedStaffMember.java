package at.wrk.coceso.parser.staff;

import at.wrk.coceso.dto.contact.ContactDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

/**
 * This can be implemented and exposed as a bean by plugins to allow parsing staff members from a specific CSV format
 */
@Getter
@Setter
@NoArgsConstructor
public class ParsedStaffMember {

    private String externalId;
    private String firstname;
    private String lastname;
    private String info;
    private Collection<Integer> personnelId;
    private Collection<ContactDto> contacts;
}
