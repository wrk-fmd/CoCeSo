package at.wrk.coceso.utils;

/**
 * Strings to be written automatically to Log
 */
@Deprecated
public class LogText {
    public static final String UNIT = "Unit";
    public static final String UNIT_ASSIGN = UNIT+" assigned";
    public static final String UNIT_DETACH = UNIT+" detached";
    public static final String UNIT_AUTO_DETACH = UNIT+" auto-detached"; //IncidentState on Done or TaskState on Detached
    public static final String UNIT_TASKSTATE_CHANGED = "TaskState changed";
    public static final String UNIT_AUTOSET_POSITION = "Position set by State Update";

    public static final String SEND_HOME_AUTO_DETACH = "Auto-Detached by Send-Home";
    public static final String SEND_HOME_ASSIGN = "New SendHome Incident assigned";

    public static final String INCIDENT_NO_UNIT_ATTACHED = "No "+UNIT+" Attached, Set to Done";
    public static final String INCIDENT_AUTO_STATE = "IncidentState Auto Set on State Update";
    public static final String INCIDENT_UPDATE = "Incident updated";
    public static final String INCIDENT_NEW = "Incident created";
    public static final String INCIDENT_DELETE = "Incident deleted";
    public static final String UNIT_NEW = UNIT+" created";
    public static final String UNIT_DELETE = UNIT + " deleted";
    public static final String UNIT_UPDATE = UNIT + " updated";
}
