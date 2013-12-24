package at.wrk.coceso.entity;

public class Point {

    public Point() {
        super();
    }

    public Point(String info) {
        this.info = info;
    }

    private int id;

    private String info;

    private double longitude;

    private double latitude;

    public void prepareNotNull() {
        if(info == null) info = "";
    }

    @Override
    public String toString() {
        return info+"";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
