package at.wrk.coceso.entity.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * Slim Version of <code>UnitContainer</code>
 * List <code>units</code> contains only IDs of the Units
 */
public class SlimUnitContainer {
    private int id;
    private String name;

    private List<SlimUnitContainer> subContainer;
    private List<Integer> unitIds;

    public SlimUnitContainer() {
        super();
    }

    public SlimUnitContainer(UnitContainer container) {
        this.id = container.getId();
        this.name = container.getName();

        subContainer = new LinkedList<>();
        for(UnitContainer sub : container.getSubContainer()) {
            subContainer.add(new SlimUnitContainer(sub));
        }

        unitIds = new LinkedList<>();
        for(SlimUnit unit : container.getUnits()) {
            unitIds.add(unit.getId());
        }
    }

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

    public List<SlimUnitContainer> getSubContainer() {
        return subContainer;
    }

    public void setSubContainer(List<SlimUnitContainer> subContainer) {
        this.subContainer = subContainer;
    }

    public List<Integer> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Integer> unitIds) {
        this.unitIds = unitIds;
    }
}
