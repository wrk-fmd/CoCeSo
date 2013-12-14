package at.wrk.coceso.entity;

public class Concern {

    public int id;

    public String name;

    public Point place;

    public String info;

    public int pax;

    public boolean closed;

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
}
