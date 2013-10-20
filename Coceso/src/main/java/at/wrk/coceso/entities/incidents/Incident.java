package at.wrk.coceso.entities.incidents;


import at.wrk.coceso.entities.*;

import java.util.*;

public class Incident {
    protected int id;

    protected Case aCase;

    protected IncidentState state;

    protected int priority;

    protected boolean blue;

    protected Map<Unit, TaskState> units;

    protected CocesoPOI bo;

    protected CocesoPOI ao;

    protected String info;

    protected String caller;


}
