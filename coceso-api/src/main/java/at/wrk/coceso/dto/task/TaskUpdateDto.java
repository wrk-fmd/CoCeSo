package at.wrk.coceso.dto.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskUpdateDto {

    private TaskStateDto state;
}
