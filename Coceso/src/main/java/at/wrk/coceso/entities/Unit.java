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
}
