package at.wrk.coceso.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class SystemTimeDto {

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true)
    private Instant time;
}
