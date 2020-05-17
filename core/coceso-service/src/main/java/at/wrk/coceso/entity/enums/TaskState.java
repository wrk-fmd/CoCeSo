package at.wrk.coceso.entity.enums;

public enum TaskState {
    Assigned, ZBO, ABO, ZAO, AAO, Detached;

    public boolean isWorking() {
        return this == ABO || this == ZAO || this == AAO;
    }
}
