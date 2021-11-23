package at.wrk.coceso.dto.incident;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IncidentBriefDto {

    @Schema(required = true)
    private Long id;
}
