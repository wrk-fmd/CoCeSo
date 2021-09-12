package at.wrk.coceso.dto.staff;

import at.wrk.coceso.dto.contact.ContactDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class StaffMemberDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private String firstname;

    @Schema(required = true)
    private String lastname;

    @Schema(required = true)
    private String info;

    @Schema(required = true)
    private Collection<Integer> personnelId;

    @Schema(required = true)
    private Collection<ContactDto> contacts;
}
