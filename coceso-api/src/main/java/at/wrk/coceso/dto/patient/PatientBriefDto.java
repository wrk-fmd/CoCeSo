package at.wrk.coceso.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientBriefDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private String lastname;

    @Schema(required = true)
    private String firstname;
}
