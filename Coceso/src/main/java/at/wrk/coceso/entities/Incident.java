package at.wrk.coceso.entities;


import java.util.*;

public class Incident {
    private int id;

    private Case aCase;

    private IncidentState state;

    private int priority;

    private boolean blue;

    private Map<Unit, TaskState> units;

    private CocesoPOI bo;

    private CocesoPOI ao;

    private String info;

}
