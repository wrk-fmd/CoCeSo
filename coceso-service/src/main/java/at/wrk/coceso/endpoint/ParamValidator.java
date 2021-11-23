package at.wrk.coceso.endpoint;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.exceptions.ConcernClosedException;
import at.wrk.coceso.exceptions.ConcernMismatchException;
import at.wrk.coceso.exceptions.NotFoundException;

public class ParamValidator {

    /**
     * Validate that the concern exists
     *
     * @param concern The concern, which is not null iff it exists
     */
    public static void exists(Concern concern) {
        if (concern == null) {
            throw new NotFoundException("The concern does not exist");
        }
    }

    /**
     * Validate that the staff member exists
     *
     * @param staffMember The staff member, which is not null iff it exists
     */
    public static void exists(StaffMember staffMember) {
        if (staffMember == null) {
            throw new NotFoundException("The staff member does not exist");
        }
    }

    /**
     * Validate that the concern exists and is not closed
     *
     * @param concern The concern, which is not null iff it exists
     */
    public static void open(Concern concern) {
        exists(concern);
        if (concern.isClosed()) {
            throw new ConcernClosedException();
        }
    }

    public static void open(Concern concern, Incident incident) {
        open(concern);
        matches(concern, incident);
    }

    public static void open(Concern concern, Incident incident, Patient patient) {
        open(concern);
        matches(concern, incident);
        matches(concern, patient);
    }

    public static void open(Concern concern, Incident incident, Unit unit) {
        open(concern);
        matches(concern, incident);
        matches(concern, unit);
    }

    public static void open(Concern concern, Patient patient) {
        open(concern);
        matches(concern, patient);
    }

    public static void open(Concern concern, Unit unit) {
        open(concern);
        matches(concern, unit);
    }

    public static void open(Concern concern, Container container) {
        open(concern);
        matches(concern, container);
    }

    public static void open(Concern concern, Container container, Unit unit) {
        open(concern);
        matches(concern, container);
        matches(concern, unit);
    }

    public static void matches(Concern concern, Incident incident) {
        if (incident == null) {
            throw new NotFoundException("The incident does not exist");
        }
        if (!incident.getConcern().equals(concern)) {
            throw new ConcernMismatchException("The concern for the incident does not match");
        }
    }

    private static void matches(Concern concern, Patient patient) {
        if (patient == null) {
            throw new NotFoundException("The patient does not exist");
        }
        if (!patient.getConcern().equals(concern)) {
            throw new ConcernMismatchException("The concern for the patient does not match");
        }
    }

    public static void matches(Concern concern, Unit unit) {
        if (unit == null) {
            throw new NotFoundException("The unit does not exist");
        }
        if (!unit.getConcern().equals(concern)) {
            throw new ConcernMismatchException("The concern for the unit does not match");
        }
    }

    private static void matches(Concern concern, Container container) {
        if (container == null) {
            throw new NotFoundException("The container does not exist");
        }
        if (!container.getConcern().equals(concern)) {
            throw new ConcernMismatchException("The concern for the container does not match");
        }
    }
}
