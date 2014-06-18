package at.wrk.coceso.entity.enums;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum IncidentType {
    HoldPosition (new TaskState[] {TaskState.Assigned, TaskState.AAO, TaskState.Detached}, true),
    Standby      (new TaskState[] {TaskState.Assigned, TaskState.AAO, TaskState.Detached}, true),
    Relocation (new TaskState[] {TaskState.Assigned, TaskState.ZAO, TaskState.AAO, TaskState.Detached}),
    ToHome     (new TaskState[] {TaskState.Assigned, TaskState.ZAO, TaskState.AAO, TaskState.Detached}, true),
    Task,
    Transport;


    private List<TaskState> possibleStates;


    private boolean singleUnit;

    IncidentType() {
        this(TaskState.orderedValues(), false);
    }

    IncidentType(TaskState[] possibleStates) {
        this(possibleStates, false);
    }

    IncidentType(TaskState[] possibleStates, boolean singleUnit) {
        this.possibleStates = new ArrayList<TaskState>();
        Collections.addAll(this.possibleStates, possibleStates);

        this.singleUnit = singleUnit;
    }

    public List<TaskState> getPossibleStates() {
        return possibleStates;
    }

    public boolean isSingleUnit() {
        return singleUnit;
    }

    public boolean isPossibleState(TaskState state) {
        return possibleStates.contains(state);
    }

}

