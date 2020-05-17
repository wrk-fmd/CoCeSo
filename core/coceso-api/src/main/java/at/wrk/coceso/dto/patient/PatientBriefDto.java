package at.wrk.coceso.dto.patient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientBriefDto {

    private Long id;
    private String lastname;
    private String firstname;
}
