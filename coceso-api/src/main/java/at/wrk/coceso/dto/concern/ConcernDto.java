package at.wrk.coceso.dto.concern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class ConcernDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private String info;

    @Schema(required = true)
    private boolean closed;

    @Schema(required = true)
    private Collection<String> sections;
}
