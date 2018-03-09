package at.wrk.coceso.entity.enums;

public enum IncidentState {
  Open, Demand, InProgress, Done;

  public boolean isOpen() {
    return this == Open || this == Demand;
  }

  public boolean isDone() {
    return this == Done;
  }

}
