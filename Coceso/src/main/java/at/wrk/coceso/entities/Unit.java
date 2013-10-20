package at.wrk.coceso.entities;


import java.util.List;

public class Unit {

    private int id;

    private Case aCase;

    private UnitState state;

    private String call;

    private String ani; // Radio ID

    private boolean withDoc;

    private boolean portable;

    private boolean transportVehicle;

    private List<Person> crew;

    private String info;

    private CocesoPOI position;

    private CocesoPOI home;
}
