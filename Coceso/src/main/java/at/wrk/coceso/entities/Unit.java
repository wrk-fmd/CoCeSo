package at.wrk.coceso.entities;


import java.util.List;

public class Unit {

    public int id;

    public Case aCase;

    public UnitState state;

    public String call;

    public String ani; // Radio ID

    public boolean withDoc;

    public boolean portable;

    public boolean transportVehicle;

    public List<Person> crew;

    public String info;

    public CocesoPOI position;

    public CocesoPOI home;

    /**
     * Default Values for Insert in Database
     */
    public void prepareNotNull(){
        if(state == null) state = UnitState.AD;
        if(call == null) call = "";
        if(ani == null) ani = "";
        if(info == null) info = "";
    }

    public int getId() {
        return id;
    }

    public String getCall() {
        return call;
    }

    public String getAni() {
        return ani;
    }

    public boolean isWithDoc() {
        return withDoc;
    }

    public boolean isPortable() {
        return portable;
    }

    public boolean isTransportVehicle() {
        return transportVehicle;
    }

    public String getInfo() {
        return info;
    }
}
