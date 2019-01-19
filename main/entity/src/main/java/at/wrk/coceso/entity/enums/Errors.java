package at.wrk.coceso.entity.enums;

public enum Errors {

    ConcernMissingOrClosed(1),
    ConcernMissing(2),
    ConcernClosed(3),
    ConcernOpen(4),
    NotAuthorized(5),
    ConcernName(6),
    Connection(7),
    NullPointer(8),
    Database(9),
    Validation(10),
    ContainerCycle(11),
    ContainerMultipleRoots(12),
    ConcernMismatch(13),
    SectionEmpty(13),
    EntityMissing(14),
    SectionExists(14),
    Constraint(15),
    IncidentMissing(16),
    IncidentNotAllowed(17),
    NotTreatment(18),
    PatientDone(19),
    ImpossibleTaskState(20),
    MultipleUnits(21),
    UnitCreateNotAllowed(22),
    PatientCreateNotAllowed(23),
    UnitLocked(24),
    Import(25),

    NoTargetsForAlarmText(60),
    NoGatewayForAlarmTextConfigured(61),

    HttpUnauthorized(401, "Unauthorized"),
    HttpAccessDenied(403, "Forbidden"),
    HttpNotFound(404, "Not found");

    private final int code;
    private final String message;

    Errors(int code) {
        this(code, null);
    }

    Errors(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message == null ? name() : message;
    }

}
