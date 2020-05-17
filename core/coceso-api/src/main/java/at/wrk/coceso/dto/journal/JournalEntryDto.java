package at.wrk.coceso.dto.journal;

import at.wrk.coceso.dto.task.TaskStateDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class JournalEntryDto {

    private Long id;
    private Instant timestamp;
    private Long unit;
    private Long incident;
    private Long patient;
    private TaskStateDto state;
    private String type;
    private String username;
    private String text;
    private Collection<ChangeDto> changes;
}
