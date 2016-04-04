package at.wrk.cocecl.dto;

import java.io.Serializable;

public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String call;
    private UnitState state;

    private Unit(final int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCall() {
        return call;
    }

    public void setCall(final String call) {
        this.call = call;
    }

    public UnitState getState() {
        return state;
    }

    public void setState(final UnitState state) {
        this.state = state;
    }

    public static Unit create(final int id) {
        return new Unit(id);
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
                "id=" + id + "," +
                "call=" + call + "," +
                "state=" + state +
                "]";
    }
}
