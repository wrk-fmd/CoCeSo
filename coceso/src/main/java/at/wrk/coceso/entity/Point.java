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

    public void setLatLong(Point p) {
      this.latitude = p.latitude;
      this.longitude = p.longitude;
    }

    public static boolean isEmpty(Point point) {
        return point == null || point.getInfo() == null || point.getInfo().isEmpty();

    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Point) ){
            return false;
        }
        Point that = (Point) obj;
        boolean ret = true;
        if(this.id > 0 && that.id > 0)
            ret = this.id == that.id;

        ret = ret && (this.info == null ? that.info == null : this.info.equals(that.info)) &&
                this.latitude == that.latitude &&
                this.longitude == that.longitude;

        return ret;
    }
}