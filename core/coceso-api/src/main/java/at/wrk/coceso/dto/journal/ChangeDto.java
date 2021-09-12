package at.wrk.coceso.dto.journal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeDto {

    @Schema(required = true)
    private String key;

    @Schema(required = true, nullable = true)
    private Object oldValue;

    @Schema(required = true, nullable = true)
    private Object newValue;
}
