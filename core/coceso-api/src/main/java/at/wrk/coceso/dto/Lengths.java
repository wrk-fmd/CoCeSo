package at.wrk.coceso.dto;

/**
 * This class contains max lengths for some strings to keep it consistent between validation and data model
 */
public class Lengths {

    public static final int CONCERN_NAME = 100;
    public static final int CONCERN_INFO = 255;

    public static final int SECTION_NAME = 30;

    public static final int INCIDENT_INFO = 255;
    public static final int INCIDENT_CASUS = 100;
    public static final int INCIDENT_CALLER = 100;

    public static final int UNIT_CALL = 64;
    public static final int UNIT_INFO = 255;

    public static final int PATIENT_LASTNAME = 64;
    public static final int PATIENT_FIRSTNAME = 64;
    public static final int PATIENT_EXTERNAL = 40;
    public static final int PATIENT_INSURANCE = 40;
    public static final int PATIENT_DIAGNOSIS = 255;
    public static final int PATIENT_ER_TYPE = 40;
    public static final int PATIENT_INFO = 255;

    public static final int CONTAINER_NAME = 60;

    public static final int STAFF_EXTERNAL_ID = 64;
    public static final int STAFF_LASTNAME = 64;
    public static final int STAFF_FIRSTNAME = 64;
    public static final int STAFF_INFO = 255;

    public static final int CONTACT_TYPE = 30;
    public static final int CONTACT_DATA = 100;

    public static final int LOG_TEXT = 255;
}
