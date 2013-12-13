package at.wrk.coceso.entities;

public class Point {

    public int id;

    public String info;

    public double longitude;

    public double latitude;

    public void prepareNotNull() {
        if(info == null) info = "";
    }

    @Override
    public String toString() {
        return info+"";
    }
}
