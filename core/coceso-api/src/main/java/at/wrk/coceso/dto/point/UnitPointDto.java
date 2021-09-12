package at.wrk.coceso.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.awt.Point;

@Getter
@Setter
public class UnitPointDto extends PointDto {

    @Schema(required = true)
    private Long id;
}
