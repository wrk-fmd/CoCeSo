package at.wrk.coceso.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointDto {

    @Schema(required = true)
    private String info;
}
