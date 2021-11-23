package at.wrk.coceso.dto.concern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConcernBriefDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private String name;
}
