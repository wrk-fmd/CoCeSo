package at.wrk.coceso.entity;

public class Patient {

    private int incident_id;

    private String given_name;
    private String sur_name;
    // String for International use
    private String insurance_number;

    private String diagnosis;
    private String erType; //TODO find a better name...

    private String info;

    private String externalID;

    public int getIncident_id() {
        return incident_id;
    }

    public void setIncident_id(int incident_id) {
        this.incident_id = incident_id;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getSur_name() {
        return sur_name;
    }

    public void setSur_name(String sur_name) {
        this.sur_name = sur_name;
    }

    public String getInsurance_number() {
        return insurance_number;
    }

    public void setInsurance_number(String insurance_number) {
        this.insurance_number = insurance_number;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getErType() {
        return erType;
    }

    public void setErType(String erType) {
        this.erType = erType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

}
