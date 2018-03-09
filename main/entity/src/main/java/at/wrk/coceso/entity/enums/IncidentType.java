package at.wrk.coceso.entity.enums;

import java.util.Collection;
import java.util.EnumSet;

public enum IncidentType {

  HoldPosition(true, EnumSet.of(TaskState.Assigned, TaskState.AAO, TaskState.Detached)),
  Standby(true, EnumSet.of(TaskState.Assigned, TaskState.AAO, TaskState.Detached)),
  Relocation(false, EnumSet.of(TaskState.Assigned, TaskState.ZAO, TaskState.AAO, TaskState.Detached)),
  ToHome(true, EnumSet.of(TaskState.Assigned, TaskState.ZAO, TaskState.AAO, TaskState.Detached)),
  Treatment(false, EnumSet.noneOf(TaskState.class)),
  Task,
  Transport;

  private final Collection<TaskState> possibleStates;
  private final boolean singleUnit;

  IncidentType() {
    this.possibleStates = EnumSet.allOf(TaskState.class);
    this.singleUnit = false;
  }

  IncidentType(boolean singleUnit, EnumSet<TaskState> possibleStates) {
    this.possibleStates = possibleStates;
    this.singleUnit = singleUnit;
  }

  public boolean isSingleUnit() {
    return singleUnit;
  }

  public boolean isPossibleState(TaskState state) {
    return possibleStates.contains(state);
  }

}
