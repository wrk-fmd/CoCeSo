package at.wrk.coceso.dto.unit;

import at.wrk.coceso.dto.contact.ContactDto;
import at.wrk.coceso.dto.point.PointDto;
import at.wrk.coceso.dto.staff.StaffMemberDto;
import at.wrk.coceso.dto.task.TaskDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UnitDto {

    private Long id;
    private Long concern;
    private String call;
    private UnitStateDto state;
    private Collection<UnitTypeDto> types;

    private boolean portable;

    private String info;
    private PointDto position;
    private PointDto home;
    private String section;

    private Collection<StaffMemberDto> crew;
    private Collection<ContactDto> contacts;
    private Collection<TaskDto> incidents;
}
