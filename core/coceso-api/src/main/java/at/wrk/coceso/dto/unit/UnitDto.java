package at.wrk.coceso.dto.unit;

import at.wrk.coceso.dto.contact.ContactDto;
import at.wrk.coceso.dto.point.PointDto;
import at.wrk.coceso.dto.staff.StaffMemberDto;
import at.wrk.coceso.dto.task.TaskDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class UnitDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private Long concern;

    @Schema(required = true)
    private String call;

    @Schema(required = true)
    private UnitStateDto state;

    @Schema(required = true)
    private Collection<UnitTypeDto> types;

    @Schema(required = true)
    private boolean portable;

    @Schema(required = true)
    private String info;

    @Schema(required = true, nullable = true)
    private PointDto position;

    @Schema(required = true, nullable = true)
    private PointDto home;

    @Schema(required = true, nullable = true)
    private String section;

    @Schema(required = true)
    private Collection<StaffMemberDto> crew;

    @Schema(required = true)
    private Collection<ContactDto> contacts;

    @Schema(required = true)
    private Collection<TaskDto> incidents;
}
