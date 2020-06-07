package at.wrk.coceso.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReceivedMessageDto {

    private Long id;
    private String type;
    private String channel;
    private String sender;
    private boolean emergency;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant timestamp;
}
