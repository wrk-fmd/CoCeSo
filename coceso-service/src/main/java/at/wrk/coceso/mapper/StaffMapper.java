package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.contact.ContactDto;
import at.wrk.coceso.dto.staff.StaffMemberBriefDto;
import at.wrk.coceso.dto.staff.StaffMemberDto;
import at.wrk.coceso.entity.Contact;
import at.wrk.coceso.entity.StaffMember;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    StaffMemberDto staffMemberToDto(StaffMember staffMember);

    StaffMemberBriefDto staffMemberToBriefDto(StaffMember staffMember);

    Set<ContactDto> contactsToDtos(Collection<Contact> contact);

    Set<Contact> contactDtosToContacts(Collection<ContactDto> dtos);

    default String contactsToString(Collection<Contact> contacts) {
        return contacts != null && !contacts.isEmpty() ? contacts.stream().map(Contact::toString).collect(Collectors.joining(", ")) : null;
    }
}
