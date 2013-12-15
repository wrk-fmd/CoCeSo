package at.wrk.coceso.entity;

public class Point {

    public Point() {
        super();
    }

    public Point(String info) {
        this.info = info;
    }

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
