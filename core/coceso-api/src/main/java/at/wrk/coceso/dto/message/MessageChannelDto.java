package at.wrk.coceso.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageChannelDto {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String name;
}
