package at.wrk.coceso.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PatientDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private Long concern;

    @Schema(required = true)
    private String lastname;

    @Schema(required = true)
    private String firstname;

    @Schema(required = true)
    private String externalId;

    @Schema(required = true, nullable = true)
    private SexDto sex;

    @Schema(required = true)
    private String insurance;

    @Schema(required = true, nullable = true)
    private LocalDate birthday;

    @Schema(required = true)
    private String diagnosis;

    @Schema(required = true)
    private String erType;

    @Schema(required = true)
    private String info;
}
