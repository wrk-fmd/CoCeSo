package at.wrk.coceso.dto.patient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PatientDto {

    private Long id;
    private Long concern;
    private String lastname;
    private String firstname;
    private String externalId;
    private SexDto sex;
    private String insurance;
    private LocalDate birthday;
    private String diagnosis;
    private String erType;
    private String info;
}
