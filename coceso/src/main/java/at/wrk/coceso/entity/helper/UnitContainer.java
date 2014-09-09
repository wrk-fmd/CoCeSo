package at.wrk.coceso.entity.helper;


import java.util.List;

/**
 * Full Version of Container
 * List <code>units</code> contains Objects of <code>SlimUnit</code>
 */
public class UnitContainer {
    private int id;
    private int concernId;
    private double ordering;
    private int head;

    private String name;
    private List<UnitContainer> subContainer;
    private List<SlimUnit> units;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UnitContainer> getSubContainer() {
        return subContainer;
    }

    public void setSubContainer(List<UnitContainer> subContainer) {
        this.subContainer = subContainer;
    }

    public List<SlimUnit> getUnits() {
        return units;
    }

    public void setUnits(List<SlimUnit> units) {
        this.units = units;
    }

    public int getConcernId() {
        return concernId;
    }

    public void setConcernId(int concernId) {
        this.concernId = concernId;
    }

    public double getOrdering() {
        return ordering;
    }

    public void setOrdering(double ordering) {
        this.ordering = ordering;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "UnitContainer ("+this.id+"): "+this.name+", ordering="+this.getOrdering();
    }
}
