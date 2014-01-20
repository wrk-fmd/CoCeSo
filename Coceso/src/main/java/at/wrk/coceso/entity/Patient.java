package at.wrk.coceso.entity;

public class Patient {
    private int id;

    private String given_name;
    private String sur_name;
    // String for International use
    private String insurance_number;

    private String diagnosis;
    private String typeOfER; //TODO find a better name...

    private String info;

    private Integer externalID;
}
