package at.wrk.coceso.plugin.geobroker.action.context;

import at.wrk.coceso.service.TaskWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringActionRunnerContext implements ActionRunnerContext {
    private final TaskWriteService taskWriteService;

    @Autowired
    public SpringActionRunnerContext(final TaskWriteService taskWriteService) {
        this.taskWriteService = taskWriteService;
    }

    @Override
    public TaskWriteService getTaskWriteService() {
        return taskWriteService;
    }
}
