package at.wrk.coceso.entities;

public class Case {

    public int id;

    public String name;

    public CocesoPOI place;

    public String organiser;

    public int pax;

    public void prepareNotNull() {
        if(name == null) name = "";
        if(organiser == null) organiser = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrganiser() {
        return organiser;
    }

    public int getPax() {
        return pax;
    }
}
