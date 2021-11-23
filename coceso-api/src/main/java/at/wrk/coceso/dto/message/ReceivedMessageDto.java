package at.wrk.coceso.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReceivedMessageDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private String type;

    @Schema(required = true, nullable = true)
    private String channel;

    @Schema(required = true)
    private String sender;

    @Schema(required = true)
    private boolean emergency;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true)
    private Instant timestamp;
}
