package at.wrk.coceso.entity;


import java.io.Serializable;

public class Person implements Serializable {
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

    public Person(String given_name, String sur_name, int dNr, String contact) {
        this.given_name = given_name;
        this.sur_name = sur_name;
        this.dNr = dNr;
        this.contact = contact;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (this.id > 0 && person.id > 0 && this.id != person.id ) return false;

        if (dNr != person.dNr) return false;
        //if (contact != null ? !contact.equals(person.contact) : person.contact != null) return false;
        if (given_name != null ? !given_name.equals(person.given_name) : person.given_name != null) return false;
        if (sur_name != null ? !sur_name.equals(person.sur_name) : person.sur_name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = given_name != null ? given_name.hashCode() : 0;
        result = 31 * result + (sur_name != null ? sur_name.hashCode() : 0);
        result = 31 * result + dNr;
        //result = 31 * result + (contact != null ? contact.hashCode() : 0);
        return result;
    }
}
