package at.wrk.coceso.dto.unit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnitBriefDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private String call;
}
