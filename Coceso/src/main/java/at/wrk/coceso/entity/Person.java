package at.wrk.coceso.entity;


public class Person {
    private int id;

    private String given_name;
    private String sur_name;
    private int dNr;
    private String contact;

    public Person() {
        super();
    }

    public Person(Person person) {
        super();
        this.id = person.id;
        this.given_name = person.given_name;
        this.sur_name = person.sur_name;
        this.dNr = person.dNr;
        this.contact = person.contact;
    }

    public String getGiven_name() {
        return given_name;
    }

    public String getSur_name() {
        return sur_name;
    }

    public int getId() {
        return id;
    }

    public int getdNr() {
        return dNr;
    }

    public String getContact() {
        return contact;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public void setSur_name(String sur_name) {
        this.sur_name = sur_name;
    }

    public void setdNr(int dNr) {
        this.dNr = dNr;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
