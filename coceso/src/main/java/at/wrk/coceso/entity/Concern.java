package at.wrk.coceso.entity;

import java.io.Serializable;

public class Concern implements Serializable {

    private int id;

    private String name;

    private Point place;

    private String info;

    private int pax;

    private boolean closed;

    public Concern() {
    }

    public Concern(int id) {
      this.id = id;
    }

    public void prepareNotNull() {
        if(name == null) name = "";
        if(info == null) info = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public int getPax() {
        return pax;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Concern))
            return false;
        return this.id == ((Concern) obj).id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPlace() {
        return place;
    }

    public void setPlace(Point place) {
        this.place = place;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPax(int pax) {
        this.pax = pax;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
