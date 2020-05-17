package at.wrk.coceso.dto.incident;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.dto.point.PointDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class IncidentUpdateDto {

    private IncidentTypeDto type;
    private IncidentClosedReasonDto closed;

    private Boolean priority;
    private Boolean blue;

    private PointDto bo;
    private PointDto ao;

    @Length(max = Lengths.INCIDENT_INFO)
    private String info;

    @Length(max = Lengths.INCIDENT_CASUS)
    private String casusNr;

    @Length(max = Lengths.INCIDENT_CALLER)
    private String caller;

    private String section;

    public void setCasusNr(String casusNr) {
        this.casusNr = casusNr != null ? casusNr.trim() : null;
    }

    public void setInfo(String info) {
        this.info = info != null ? info.trim() : null;
    }

    public void setCaller(String caller) {
        this.caller = caller != null ? caller.trim() : null;
    }

    public void setSection(String section) {
        this.section = section != null ? section.trim() : null;
    }
}