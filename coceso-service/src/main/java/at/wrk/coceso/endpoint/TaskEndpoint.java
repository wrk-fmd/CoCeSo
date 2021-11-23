package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.task.TaskStateDto;
import at.wrk.coceso.dto.task.TaskUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concerns/{concern}/incidents/{incident}/units/{unit}")
public class TaskEndpoint {

    private final TaskService taskService;

    @Autowired
    public TaskEndpoint(final TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).TASK_EDIT)")
    @PostMapping
    public void assign(@PathVariable final Concern concern, @PathVariable final Incident incident, @PathVariable final Unit unit) {
        ParamValidator.open(concern, incident, unit);
        taskService.assignUnit(incident, unit);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).TASK_EDIT)")
    @PutMapping
    public void updateState(@PathVariable final Concern concern, @PathVariable final Incident incident, @PathVariable final Unit unit,
            @RequestBody final TaskUpdateDto data) {
        ParamValidator.open(concern, incident, unit);
        taskService.changeState(incident, unit, data);
    }
}
