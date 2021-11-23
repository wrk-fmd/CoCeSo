package at.wrk.coceso.dto.container;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContainerDto {

    @Schema(required = true, nullable = true)
    private Long id;

    @Schema(required = true)
    private Long concern;

    @Schema(required = true, nullable = true)
    private Long parent;

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private List<Long> children;

    @Schema(required = true)
    private List<Long> units;
}
