package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.task.TaskDto;
import at.wrk.coceso.dto.task.TaskStateDto;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.enums.TaskState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "incident", source = "incident.id")
    @Mapping(target = "unit", source = "unit.id")
    TaskDto taskToDto(Task task);

    TaskState dtoToTaskState(TaskStateDto dto);

    TaskStateDto taskStateToDto(TaskState state);
}
