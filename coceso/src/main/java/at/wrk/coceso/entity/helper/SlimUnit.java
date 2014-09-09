package at.wrk.coceso.entity.helper;

public class SlimUnit implements Comparable<SlimUnit> {
    private int id;
    private String call;
    private double ordering;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public double getOrdering() {
        return ordering;
    }

    public void setOrdering(double ordering) {
        this.ordering = ordering;
    }

    @Override
    public int compareTo(SlimUnit o) {
        if(o == null)
            throw new NullPointerException();

        return this.ordering < o.ordering ? -1 : this.ordering == o.ordering ? 0 : 1;
    }
}