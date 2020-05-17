package at.wrk.coceso.dto.unit;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.dto.contact.ContactDto;
import at.wrk.coceso.dto.point.PointDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class UnitCreateDto {

    @NotNull
    @Length(min = 1, max = Lengths.UNIT_CALL)
    private String call;

    private Collection<UnitTypeDto> types;

    private boolean portable;

    @Length(max = Lengths.UNIT_INFO)
    private String info;

    private PointDto home;
    private String section;

    private Collection<ContactDto> contacts;

    public void setCall(String call) {
        this.call = call != null ? call.trim() : null;
    }

    public void setInfo(String info) {
        this.info = info != null ? info.trim() : null;
    }

    public void setSection(String section) {
        this.section = section != null ? section.trim() : null;
    }
}
