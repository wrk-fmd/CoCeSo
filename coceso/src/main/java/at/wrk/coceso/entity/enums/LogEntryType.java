package at.wrk.coceso.entity.enums;


public enum LogEntryType {
    CONCERN_CREATE("Concern created"),
    CONCERN_UPDATE("Concern updated"),
    CONCERN_REMOVE("Concern removed"),
    CONCERN_CLOSE("Concern closed"),
    CONCERN_REOPEN("Concern reopened"),

    PATIENT_CREATE("Patient created"),
    PATIENT_UPDATE("Patient updated"),

    UNIT_CREATE("Unit created"),
    UNIT_CREATE_REMOVED("Unit created - REMOVED"),
    UNIT_UPDATE("Unit updated"),
    UNIT_DELETE("Unit deleted"),
    UNIT_ASSIGN("Unit assigned"),
    UNIT_DETACH("Unit detached"),
    UNIT_AUTO_DETACH("Unit auto-detached"),
    UNIT_AUTOSET_POSITION("Position set by State Update"),

    TASKSTATE_CHANGED("TaskState changed"),

    INCIDENT_CREATE("Incident created"),
    INCIDENT_UPDATE("Incident updated"),
    INCIDENT_DELETE("Incident deleted"),
    INCIDENT_AUTO_STATE("IncidentState Auto-Set on Update"),
    INCIDENT_AUTO_DONE("IncidentState set to Done, No Unit Attached"),

    CUSTOM("Empty custom Message!")
    ;

    private String message;

    private LogEntryType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public LogEntryType customMessage(String message) {
        if(this == CUSTOM) {
            if(message != null && !message.isEmpty())
                this.message = message;
            return this;
        }
        throw new UnsupportedOperationException();
    }
}
