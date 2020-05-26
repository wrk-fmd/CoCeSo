package at.wrk.coceso.entity.enums;

import java.util.Collection;
import java.util.EnumSet;

public enum IncidentType {

    Standby(EnumSet.of(TaskState.Assigned, TaskState.ABO, TaskState.Detached)),
    ToHome(EnumSet.of(TaskState.Assigned, TaskState.ZBO, TaskState.ABO, TaskState.Detached)),
    Position(EnumSet.of(TaskState.Assigned, TaskState.ZBO, TaskState.ABO, TaskState.Detached)),
    Task,
    Transport;

    private final Collection<TaskState> possibleStates;

    IncidentType() {
        this.possibleStates = EnumSet.allOf(TaskState.class);
    }

    IncidentType(EnumSet<TaskState> possibleStates) {
        this.possibleStates = possibleStates;
    }

    public boolean isPossibleState(TaskState state) {
        return possibleStates.contains(state);
    }
}
