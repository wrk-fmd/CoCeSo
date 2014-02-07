package at.wrk.coceso.entity.enums;


public enum LogEntryType {
    UNIT_ASSIGN("Unit assigned"),
    UNIT_DETACH("Unit detached"),
    UNIT_AUTO_DETACH("Unit auto-detached"),
    TASKSTATE_CHANGED("TaskState changed"),
    UNIT_AUTOSET_POSITION("Position set by State Update");

    private String message;

    private LogEntryType(String message) {
        this.message = message;
    }
}
