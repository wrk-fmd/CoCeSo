package at.wrk.coceso.dto.journal;

import at.wrk.coceso.dto.task.TaskStateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class JournalEntryDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private Instant timestamp;

    @Schema(required = true, nullable = true)
    private Long unit;

    @Schema(required = true, nullable = true)
    private Long incident;

    @Schema(required = true, nullable = true)
    private Long patient;

    @Schema(required = true, nullable = true)
    private TaskStateDto state;

    @Schema(required = true)
    private String type;

    @Schema(required = true, nullable = true)
    private String username;

    @Schema(required = true, nullable = true)
    private String text;

    @Schema(required = true, nullable = true)
    private Collection<ChangeDto> changes;
}
